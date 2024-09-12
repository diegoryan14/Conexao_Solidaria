import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 24949,
  login: 'Sj9&@gawm8\\"U\\\'rJ\\GEh',
};

export const sampleWithPartialData: IUser = {
  id: 12163,
  login: '0C',
};

export const sampleWithFullData: IUser = {
  id: 27612,
  login: 'J',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
