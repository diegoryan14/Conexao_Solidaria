import dayjs from 'dayjs/esm';

import { IEventos, NewEventos } from './eventos.model';

export const sampleWithRequiredData: IEventos = {
  id: 11418,
};

export const sampleWithPartialData: IEventos = {
  id: 16596,
  dataEvento: dayjs('2024-09-12T00:25'),
  horaInicio: 'cut',
};

export const sampleWithFullData: IEventos = {
  id: 11754,
  nome: 'sans',
  dataCadastro: dayjs('2024-09-11T12:41'),
  dataEvento: dayjs('2024-09-11T18:48'),
  horaInicio: 'properly provided within',
  horaTermino: 'colloquy daily woot',
  observacao: 'supposing tablecloth',
};

export const sampleWithNewData: NewEventos = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
