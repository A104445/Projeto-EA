import { api } from 'boot/axios';
import type { CarreiraDTO } from 'components/models';

export const routesService = {
  getAll: () =>
    api.get<CarreiraDTO[]>('/api/routes').then((r) => r.data),
};