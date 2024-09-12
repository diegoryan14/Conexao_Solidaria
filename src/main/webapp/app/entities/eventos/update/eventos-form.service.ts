import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IEventos, NewEventos } from '../eventos.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEventos for edit and NewEventosFormGroupInput for create.
 */
type EventosFormGroupInput = IEventos | PartialWithRequiredKeyOf<NewEventos>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IEventos | NewEventos> = Omit<T, 'dataCadastro' | 'dataEvento'> & {
  dataCadastro?: string | null;
  dataEvento?: string | null;
};

type EventosFormRawValue = FormValueOf<IEventos>;

type NewEventosFormRawValue = FormValueOf<NewEventos>;

type EventosFormDefaults = Pick<NewEventos, 'id' | 'dataCadastro' | 'dataEvento'>;

type EventosFormGroupContent = {
  id: FormControl<EventosFormRawValue['id'] | NewEventos['id']>;
  nome: FormControl<EventosFormRawValue['nome']>;
  dataCadastro: FormControl<EventosFormRawValue['dataCadastro']>;
  dataEvento: FormControl<EventosFormRawValue['dataEvento']>;
  horaInicio: FormControl<EventosFormRawValue['horaInicio']>;
  horaTermino: FormControl<EventosFormRawValue['horaTermino']>;
  observacao: FormControl<EventosFormRawValue['observacao']>;
  user: FormControl<EventosFormRawValue['user']>;
};

export type EventosFormGroup = FormGroup<EventosFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EventosFormService {
  createEventosFormGroup(eventos: EventosFormGroupInput = { id: null }): EventosFormGroup {
    const eventosRawValue = this.convertEventosToEventosRawValue({
      ...this.getFormDefaults(),
      ...eventos,
    });
    return new FormGroup<EventosFormGroupContent>({
      id: new FormControl(
        { value: eventosRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nome: new FormControl(eventosRawValue.nome),
      dataCadastro: new FormControl(eventosRawValue.dataCadastro),
      dataEvento: new FormControl(eventosRawValue.dataEvento),
      horaInicio: new FormControl(eventosRawValue.horaInicio),
      horaTermino: new FormControl(eventosRawValue.horaTermino),
      observacao: new FormControl(eventosRawValue.observacao),
      user: new FormControl(eventosRawValue.user),
    });
  }

  getEventos(form: EventosFormGroup): IEventos | NewEventos {
    return this.convertEventosRawValueToEventos(form.getRawValue() as EventosFormRawValue | NewEventosFormRawValue);
  }

  resetForm(form: EventosFormGroup, eventos: EventosFormGroupInput): void {
    const eventosRawValue = this.convertEventosToEventosRawValue({ ...this.getFormDefaults(), ...eventos });
    form.reset(
      {
        ...eventosRawValue,
        id: { value: eventosRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): EventosFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dataCadastro: currentTime,
      dataEvento: currentTime,
    };
  }

  private convertEventosRawValueToEventos(rawEventos: EventosFormRawValue | NewEventosFormRawValue): IEventos | NewEventos {
    return {
      ...rawEventos,
      dataCadastro: dayjs(rawEventos.dataCadastro, DATE_TIME_FORMAT),
      dataEvento: dayjs(rawEventos.dataEvento, DATE_TIME_FORMAT),
    };
  }

  private convertEventosToEventosRawValue(
    eventos: IEventos | (Partial<NewEventos> & EventosFormDefaults),
  ): EventosFormRawValue | PartialWithRequiredKeyOf<NewEventosFormRawValue> {
    return {
      ...eventos,
      dataCadastro: eventos.dataCadastro ? eventos.dataCadastro.format(DATE_TIME_FORMAT) : undefined,
      dataEvento: eventos.dataEvento ? eventos.dataEvento.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
