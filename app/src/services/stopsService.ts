import { api } from 'boot/axios';
import type { ParagemDTO, CarreiraDTO } from 'components/models';

function currentTimeAndDay(): { time: string; day: number } {
  const now = new Date();
  const hh = now.getHours().toString().padStart(2, '0');
  const mm = now.getMinutes().toString().padStart(2, '0');
  const jsDay = now.getDay(); // 0=Dom, 1=Seg, ..., 6=Sáb
  const day = jsDay === 0 ? 7 : jsDay; // converter para 1=Seg..7=Dom
  return { time: `${hh}:${mm}`, day };
}

export const stopsService = {
  getByQr: (qr: string) =>
    api.get<ParagemDTO>('/api/stops/by-qr', { params: { qr } }).then((r) => r.data),

  getRoutes: (paragemID: number) => {
    const { time, day } = currentTimeAndDay();
    return api
      .get<CarreiraDTO[]>(`/api/stops/${paragemID}/routes`, { params: { time, day } })
      .then((r) => r.data);
  },
};