import { api } from 'boot/axios';
import type { ParagemDTO, CarreiraDTO } from 'components/models';

export const stopsService = {
  getByQr: (qr: string) =>
    api.get<ParagemDTO>('/api/stops/by-qr', { params: { qr } }).then((r) => r.data),

  getRoutes: (paragemID: number) =>
    api.get<CarreiraDTO[]>(`/api/stops/${paragemID}/routes`).then((r) => r.data),
};