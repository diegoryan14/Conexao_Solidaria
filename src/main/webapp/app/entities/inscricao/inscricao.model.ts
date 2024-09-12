import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IEventos } from 'app/entities/eventos/eventos.model';

export interface IInscricao {
  id: number;
  data?: dayjs.Dayjs | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
  evento?: IEventos | null;
}

export type NewInscricao = Omit<IInscricao, 'id'> & { id: null };
