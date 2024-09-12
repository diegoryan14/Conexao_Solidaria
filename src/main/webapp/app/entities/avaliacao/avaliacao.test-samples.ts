import { IAvaliacao, NewAvaliacao } from './avaliacao.model';

export const sampleWithRequiredData: IAvaliacao = {
  id: 5047,
};

export const sampleWithPartialData: IAvaliacao = {
  id: 9729,
  estrelas: 12066,
  observacao: 'antagonize',
};

export const sampleWithFullData: IAvaliacao = {
  id: 28441,
  estrelas: 10376,
  observacao: 'chaperon',
};

export const sampleWithNewData: NewAvaliacao = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
