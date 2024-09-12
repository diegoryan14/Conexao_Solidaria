import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IEventos } from 'app/entities/eventos/eventos.model';
import { EventosService } from 'app/entities/eventos/service/eventos.service';
import { AvaliacaoService } from '../service/avaliacao.service';
import { IAvaliacao } from '../avaliacao.model';
import { AvaliacaoFormService, AvaliacaoFormGroup } from './avaliacao-form.service';

@Component({
  standalone: true,
  selector: 'app-avaliacao-update',
  templateUrl: './avaliacao-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class AvaliacaoUpdateComponent implements OnInit {
  isSaving = false;
  avaliacao: IAvaliacao | null = null;

  usersSharedCollection: IUser[] = [];
  eventosSharedCollection: IEventos[] = [];

  protected avaliacaoService = inject(AvaliacaoService);
  protected avaliacaoFormService = inject(AvaliacaoFormService);
  protected userService = inject(UserService);
  protected eventosService = inject(EventosService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AvaliacaoFormGroup = this.avaliacaoFormService.createAvaliacaoFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareEventos = (o1: IEventos | null, o2: IEventos | null): boolean => this.eventosService.compareEventos(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ avaliacao }) => {
      this.avaliacao = avaliacao;
      if (avaliacao) {
        this.updateForm(avaliacao);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const avaliacao = this.avaliacaoFormService.getAvaliacao(this.editForm);
    if (avaliacao.id !== null) {
      this.subscribeToSaveResponse(this.avaliacaoService.update(avaliacao));
    } else {
      this.subscribeToSaveResponse(this.avaliacaoService.create(avaliacao));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAvaliacao>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(avaliacao: IAvaliacao): void {
    this.avaliacao = avaliacao;
    this.avaliacaoFormService.resetForm(this.editForm, avaliacao);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, avaliacao.user);
    this.eventosSharedCollection = this.eventosService.addEventosToCollectionIfMissing<IEventos>(
      this.eventosSharedCollection,
      avaliacao.evento,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.avaliacao?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.eventosService
      .query()
      .pipe(map((res: HttpResponse<IEventos[]>) => res.body ?? []))
      .pipe(map((eventos: IEventos[]) => this.eventosService.addEventosToCollectionIfMissing<IEventos>(eventos, this.avaliacao?.evento)))
      .subscribe((eventos: IEventos[]) => (this.eventosSharedCollection = eventos));
  }
}
