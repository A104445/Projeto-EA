import { api } from 'boot/axios';
import type { TituloDTO } from 'components/models';

export const titlesService = {
  getAll: () =>
    api.get<TituloDTO[]>('/api/tickets/mine').then((r) => r.data),

  getActive: () =>
    api.get<TituloDTO[]>('/api/tickets/mine/active').then((r) => r.data),

  buyPasseAnual: (zonaIDs: number[]) =>
    api.post('/api/tickets/annual-pass', { zonaIDs }),

  buyBilhete: (carreiraID: number) =>
    api.post('/api/tickets/single-ticket', { carreiraID }),

  buyPacote: (numViagens: number, zonaIDs: number[]) =>
    api.post('/api/tickets/trip-pack', { numViagens, zonaIDs }),
};