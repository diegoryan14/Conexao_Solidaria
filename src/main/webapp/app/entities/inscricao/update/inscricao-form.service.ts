import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IInscricao, NewInscricao } from '../inscricao.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IInscricao for edit and NewInscricaoFormGroupInput for create.
 */
type InscricaoFormGroupInput = IInscricao | PartialWithRequiredKeyOf<NewInscricao>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IInscricao | NewInscricao> = Omit<T, 'data'> & {
  data?: string | null;
};

type InscricaoFormRawValue = FormValueOf<IInscricao>;

type NewInscricaoFormRawValue = FormValueOf<NewInscricao>;

type InscricaoFormDefaults = Pick<NewInscricao, 'id' | 'data'>;

type InscricaoFormGroupContent = {
  id: FormControl<InscricaoFormRawValue['id'] | NewInscricao['id']>;
  data: FormControl<InscricaoFormRawValue['data']>;
  user: FormControl<InscricaoFormRawValue['user']>;
  evento: FormControl<InscricaoFormRawValue['evento']>;
};

export type InscricaoFormGroup = FormGroup<InscricaoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class InscricaoFormService {
  createInscricaoFormGroup(inscricao: InscricaoFormGroupInput = { id: null }): InscricaoFormGroup {
    const inscricaoRawValue = this.convertInscricaoToInscricaoRawValue({
      ...this.getFormDefaults(),
      ...inscricao,
    });
    return new FormGroup<InscricaoFormGroupContent>({
      id: new FormControl(
        { value: inscricaoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      data: new FormControl(inscricaoRawValue.data),
      user: new FormControl(inscricaoRawValue.user),
      evento: new FormControl(inscricaoRawValue.evento),
    });
  }

  getInscricao(form: InscricaoFormGroup): IInscricao | NewInscricao {
    return this.convertInscricaoRawValueToInscricao(form.getRawValue() as InscricaoFormRawValue | NewInscricaoFormRawValue);
  }

  resetForm(form: InscricaoFormGroup, inscricao: InscricaoFormGroupInput): void {
    const inscricaoRawValue = this.convertInscricaoToInscricaoRawValue({ ...this.getFormDefaults(), ...inscricao });
    form.reset(
      {
        ...inscricaoRawValue,
        id: { value: inscricaoRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): InscricaoFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      data: currentTime,
    };
  }

  private convertInscricaoRawValueToInscricao(rawInscricao: InscricaoFormRawValue | NewInscricaoFormRawValue): IInscricao | NewInscricao {
    return {
      ...rawInscricao,
      data: dayjs(rawInscricao.data, DATE_TIME_FORMAT),
    };
  }

  private convertInscricaoToInscricaoRawValue(
    inscricao: IInscricao | (Partial<NewInscricao> & InscricaoFormDefaults),
  ): InscricaoFormRawValue | PartialWithRequiredKeyOf<NewInscricaoFormRawValue> {
    return {
      ...inscricao,
      data: inscricao.data ? inscricao.data.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
