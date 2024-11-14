import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface IEventos {
  id: number;
  nome?: string | null;
  dataCadastro?: dayjs.Dayjs | null;
  dataEvento?: dayjs.Dayjs | null;
  horaInicio?: string | null;
  horaTermino?: string | null;
  observacao?: string | null;
  dataEventFormat?: string | null;
  isInscrito?: boolean; // Adicione esta linha
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewEventos = Omit<IEventos, 'id'> & { id: null };
