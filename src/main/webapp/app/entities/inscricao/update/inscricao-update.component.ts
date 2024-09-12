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
import { InscricaoService } from '../service/inscricao.service';
import { IInscricao } from '../inscricao.model';
import { InscricaoFormService, InscricaoFormGroup } from './inscricao-form.service';

@Component({
  standalone: true,
  selector: 'app-inscricao-update',
  templateUrl: './inscricao-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class InscricaoUpdateComponent implements OnInit {
  isSaving = false;
  inscricao: IInscricao | null = null;

  usersSharedCollection: IUser[] = [];
  eventosSharedCollection: IEventos[] = [];

  protected inscricaoService = inject(InscricaoService);
  protected inscricaoFormService = inject(InscricaoFormService);
  protected userService = inject(UserService);
  protected eventosService = inject(EventosService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: InscricaoFormGroup = this.inscricaoFormService.createInscricaoFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareEventos = (o1: IEventos | null, o2: IEventos | null): boolean => this.eventosService.compareEventos(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ inscricao }) => {
      this.inscricao = inscricao;
      if (inscricao) {
        this.updateForm(inscricao);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const inscricao = this.inscricaoFormService.getInscricao(this.editForm);
    if (inscricao.id !== null) {
      this.subscribeToSaveResponse(this.inscricaoService.update(inscricao));
    } else {
      this.subscribeToSaveResponse(this.inscricaoService.create(inscricao));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInscricao>>): void {
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

  protected updateForm(inscricao: IInscricao): void {
    this.inscricao = inscricao;
    this.inscricaoFormService.resetForm(this.editForm, inscricao);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, inscricao.user);
    this.eventosSharedCollection = this.eventosService.addEventosToCollectionIfMissing<IEventos>(
      this.eventosSharedCollection,
      inscricao.evento,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.inscricao?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.eventosService
      .query()
      .pipe(map((res: HttpResponse<IEventos[]>) => res.body ?? []))
      .pipe(map((eventos: IEventos[]) => this.eventosService.addEventosToCollectionIfMissing<IEventos>(eventos, this.inscricao?.evento)))
      .subscribe((eventos: IEventos[]) => (this.eventosSharedCollection = eventos));
  }
}
