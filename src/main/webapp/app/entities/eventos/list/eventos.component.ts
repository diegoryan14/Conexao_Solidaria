import { Component, NgZone, inject, OnInit, OnDestroy, Pipe, PipeTransform } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { combineLatest, filter, Observable, Subscription, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { sortStateSignal, SortDirective, SortByDirective, type SortState, SortService } from 'app/shared/sort';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { ItemCountComponent } from 'app/shared/pagination';
import { FormsModule } from '@angular/forms';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { SORT, ITEM_DELETED_EVENT, DEFAULT_SORT_DATA } from 'app/config/navigation.constants';
import { FilterComponent, FilterOptions, IFilterOptions, IFilterOption } from 'app/shared/filter';
import { IEventos } from '../eventos.model';

import { EntityArrayResponseType, EventosService } from '../service/eventos.service';
import { EventosDeleteDialogComponent } from '../delete/eventos-delete-dialog.component';
import { CustomDateTimePipe } from 'app/shared/date/custom-date-time.pipe';
import { AccountService } from 'app/core/auth/account.service';
import { FilterPipe } from './filter.pipe';

@Component({
  standalone: true,
  selector: 'app-eventos',
  templateUrl: './eventos.component.html',
  styleUrls: ['./eventos.component.scss'],
  imports: [
    RouterModule,
    FormsModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
    FilterComponent,
    ItemCountComponent,
    CustomDateTimePipe,
    FilterPipe, // Adicione o pipe aqui
  ],
})
export class EventosComponent implements OnInit, OnDestroy {
  searchTerm: string = '';
  subscription: Subscription | null = null;
  eventos?: IEventos[];
  isLoading = false;
  isInscrito: boolean = false; // Propriedade para controlar a inscrição
  sortState = sortStateSignal({});
  filters: IFilterOptions = new FilterOptions();

  itemsPerPage = ITEMS_PER_PAGE;
  totalItems = 0;
  page = 1;
  user: any = null;
  showModal: boolean = false;

  public router = inject(Router);
  protected eventosService = inject(EventosService);
  protected activatedRoute = inject(ActivatedRoute);
  protected accountService = inject(AccountService);
  protected sortService = inject(SortService);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);

  trackId = (_index: number, item: IEventos): number => this.eventosService.getEventosIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();

    this.accountService.identity().subscribe(account => {
      this.user = account;
      console.warn('Usuário logado:', this.user);
    });

    this.filters.filterChanges.subscribe(filterOptions => this.handleNavigation(1, this.sortState(), filterOptions));
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe(); // Descartando a assinatura para evitar vazamento de memória
  }

  Inscrever(index: number): void {
    if (this.eventos) {
      this.eventos[index].isInscrito = true; // Marca apenas o evento selecionado como inscrito
      console.warn(`Inscrição confirmada para: ${this.eventos[index].nome}`);
    }
  }

  confirmarInscricao(): void {
    this.isInscrito = true; // Atualiza o status de inscrição
    this.showModal = false;
  }

  VisualizarCandidatos(): void {
    console.warn('teste btn visualizar candidatos');
  }

  delete(eventos: IEventos): void {
    const modalRef = this.modalService.open(EventosDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.eventos = eventos;
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  filterEvents(): void {
    if (this.eventos) {
      this.eventos = this.eventos.filter(
        evento =>
          evento && evento.nome && typeof evento.nome === 'string' && evento.nome.toLowerCase().includes(this.searchTerm.toLowerCase()),
      );
    }
  }

  load(): void {
    this.queryBackend().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(this.page, event, this.filters.filterOptions);
  }

  navigateToPage(page: number): void {
    this.handleNavigation(page, this.sortState(), this.filters.filterOptions);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page = +(page ?? 1);
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
    this.filters.initializeFromParams(params);
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    dataFromBody.forEach(e => {
      if (e.dataEvento) {
        e.dataEventFormat = e.dataEvento.format('DD/MM/YYYY');
      }
    });
    this.eventos = dataFromBody;
  }

  protected fillComponentAttributesFromResponseBody(data: IEventos[] | null): IEventos[] {
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems = Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER));
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    const { page, filters } = this;

    this.isLoading = true;
    const pageToLoad: number = page;
    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      eagerload: true,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    filters.filterOptions.forEach(filterOption => {
      queryObject[filterOption.name] = filterOption.values;
    });
    return this.eventosService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(page: number, sortState: SortState, filterOptions?: IFilterOption[]): void {
    const queryParamsObj: any = {
      page,
      size: this.itemsPerPage,
      sort: this.sortService.buildSortParam(sortState),
    };

    filterOptions?.forEach(filterOption => {
      queryParamsObj[filterOption.nameAsQueryParam()] = filterOption.values;
    });

    this.ngZone.run(() => {
      this.router.navigate(['./'], {
        relativeTo: this.activatedRoute,
        queryParams: queryParamsObj,
      });
    });
  }
}
