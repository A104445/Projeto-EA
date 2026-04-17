import { api } from 'boot/axios';
import type { ZonaDTO } from 'components/models';

export const zonesService = {
  getAll: () =>
    api.get<ZonaDTO[]>('/api/zones').then((r) => r.data),
};