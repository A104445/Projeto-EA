import { api } from 'boot/axios';
import type { CarreiraDTO, HorarioDTO } from 'components/models';

export const routesService = {
  getAll: () =>
    api.get<CarreiraDTO[]>('/api/routes').then((r) => r.data),

  getSchedules: (carreiraID: number, day?: number) =>
    api
      .get<HorarioDTO[]>(`/api/routes/${carreiraID}/schedules`, { params: day != null ? { day } : {} })
      .then((r) => r.data),
};