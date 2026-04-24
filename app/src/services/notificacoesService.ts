import { api } from 'boot/axios';
import type { NotificacaoDTO } from 'components/models';

export const notificacoesService = {
  getAll: () =>
    api.get<NotificacaoDTO[]>('/api/notifications').then((r) => r.data),
};
