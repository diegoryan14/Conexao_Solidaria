import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '9a8fcda8-dffd-42f7-84b1-6e14f9d9c224',
};

export const sampleWithPartialData: IAuthority = {
  name: 'd6b450b1-3686-438b-b8fe-6474a692b445',
};

export const sampleWithFullData: IAuthority = {
  name: '6017d7de-2e84-4cb4-993d-bd884f9b3dc6',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
