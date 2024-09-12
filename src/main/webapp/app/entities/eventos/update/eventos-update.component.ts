import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IEventos } from '../eventos.model';
import { EventosService } from '../service/eventos.service';
import { EventosFormService, EventosFormGroup } from './eventos-form.service';

@Component({
  standalone: true,
  selector: 'app-eventos-update',
  templateUrl: './eventos-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class EventosUpdateComponent implements OnInit {
  isSaving = false;
  eventos: IEventos | null = null;

  usersSharedCollection: IUser[] = [];

  protected eventosService = inject(EventosService);
  protected eventosFormService = inject(EventosFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: EventosFormGroup = this.eventosFormService.createEventosFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ eventos }) => {
      this.eventos = eventos;
      if (eventos) {
        this.updateForm(eventos);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const eventos = this.eventosFormService.getEventos(this.editForm);
    if (eventos.id !== null) {
      this.subscribeToSaveResponse(this.eventosService.update(eventos));
    } else {
      this.subscribeToSaveResponse(this.eventosService.create(eventos));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEventos>>): void {
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

  protected updateForm(eventos: IEventos): void {
    this.eventos = eventos;
    this.eventosFormService.resetForm(this.editForm, eventos);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, eventos.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.eventos?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
