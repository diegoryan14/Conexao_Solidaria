import { IUser } from 'app/entities/user/user.model';
import { IEventos } from 'app/entities/eventos/eventos.model';

export interface IAvaliacao {
  id: number;
  estrelas?: number | null;
  observacao?: string | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
  evento?: IEventos | null;
}

export type NewAvaliacao = Omit<IAvaliacao, 'id'> & { id: null };
