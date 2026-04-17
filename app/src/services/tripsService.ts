import { api } from 'boot/axios';
import type { ViagemDTO } from 'components/models';

export const tripsService = {
  getHistory: () =>
    api.get<ViagemDTO[]>('/api/trips/history').then((r) => r.data),

  getActive: () =>
    api.get<ViagemDTO | null>('/api/trips/active').then((r) => r.data ?? null),

  checkIn: (paragemID: number, carreiraID: number, tituloID: number) =>
    api.post<ViagemDTO>('/api/trips/check-in', { paragemID, carreiraID, tituloID }).then((r) => r.data),

  checkOut: (viagemID: number, paragemID: number) =>
    api.post<ViagemDTO>(`/api/trips/${viagemID}/check-out`, { paragemID }).then((r) => r.data),
};