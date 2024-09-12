import dayjs from 'dayjs/esm';

import { IInscricao, NewInscricao } from './inscricao.model';

export const sampleWithRequiredData: IInscricao = {
  id: 28459,
};

export const sampleWithPartialData: IInscricao = {
  id: 2089,
};

export const sampleWithFullData: IInscricao = {
  id: 28050,
  data: dayjs('2024-09-11T11:45'),
};

export const sampleWithNewData: NewInscricao = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
