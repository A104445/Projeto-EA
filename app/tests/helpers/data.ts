import type {
  CarreiraDTO,
  HorarioDTO,
  NotificacaoDTO,
  ParagemDTO,
  TituloDTO,
  User,
  ViagemDTO,
  ZonaDTO,
} from '../../src/components/models';

export const defaultUser: User = {
  id: 1,
  nome: 'Joao Silva',
  email: 'joao@example.com',
  telefone: '912345678',
  dataNascimento: '2000-01-15',
};

export const defaultActiveTitles: TituloDTO[] = [
  {
    id: 101,
    tipo: 'PASSE_ANUAL',
    estado: 'ATIVO',
    valido: true,
    zonas: ['Zona A', 'Zona B'],
    dataFim: '2027-01-31',
  },
];

export const defaultAllTitles: TituloDTO[] = [
  ...defaultActiveTitles,
  {
    id: 102,
    tipo: 'PACOTE_VIAGENS',
    estado: 'ATIVO',
    valido: true,
    zonas: ['Zona C'],
    saldoRestante: 3,
  },
  {
    id: 103,
    tipo: 'BILHETE_INDIVIDUAL',
    estado: 'UTILIZADO',
    valido: false,
    zonas: ['Zona A'],
  },
];

export const defaultZones: ZonaDTO[] = [
  { id: 1, nome: 'Zona A' },
  { id: 2, nome: 'Zona B' },
  { id: 3, nome: 'Zona C' },
];

export const defaultRoutes: CarreiraDTO[] = [
  {
    id: 201,
    num: '101',
    nome: 'Aeroporto',
    paragens: [],
    proximaChegada: '10:15',
  },
  {
    id: 202,
    num: '202',
    nome: 'Universidade',
    paragens: [],
    proximaChegada: '10:25',
  },
];

export const defaultSchedulesByRoute: Record<number, HorarioDTO[]> = {
  201: [
    { id: 1, carreiraId: 201, paragemId: 1, paragemNome: 'Terminal', horaChegada: '10:15:00', diaSemana: 1 },
    { id: 2, carreiraId: 201, paragemId: 2, paragemNome: 'Campus', horaChegada: '10:40:00', diaSemana: 1 },
  ],
  202: [
    { id: 3, carreiraId: 202, paragemId: 3, paragemNome: 'Centro', horaChegada: '11:00:00', diaSemana: 1 },
  ],
};

export const defaultHistoryTrips: ViagemDTO[] = [
  {
    id: 301,
    estado: 'CONCLUIDA',
    dataHoraEntrada: '2026-04-24T08:15:00',
    dataHoraSaida: '2026-04-24T08:45:00',
    carreiraNum: '101',
    carreiraNome: 'Aeroporto',
    origemNome: 'Terminal',
    destinoNome: 'Campus',
    irregular: false,
  },
  {
    id: 302,
    estado: 'CONCLUIDA',
    dataHoraEntrada: '2026-04-23T18:10:00',
    dataHoraSaida: '2026-04-23T18:50:00',
    carreiraNum: '202',
    carreiraNome: 'Universidade',
    origemNome: 'Centro',
    destinoNome: 'Residencia',
    irregular: true,
  },
];

export const defaultNotifications: NotificacaoDTO[] = [
  {
    tipo: 'PASSE_EXPIRANDO',
    mensagem: 'O passe expira em breve.',
    tituloID: 101,
  },
  {
    tipo: 'VIAGEM_IRREGULAR',
    mensagem: 'A ultima viagem foi marcada como irregular.',
    tituloID: 102,
  },
];

export const defaultStops: ParagemDTO[] = [
  { id: 1, nome: 'Terminal', qr: 'QR-TERMINAL' },
  { id: 2, nome: 'Campus', qr: 'QR-CAMPUS' },
  { id: 3, nome: 'Centro', qr: 'QR-CENTRO' },
];

export const defaultRoutesByStop: Record<number, CarreiraDTO[]> = {
  1: [defaultRoutes[0]!],
  2: [defaultRoutes[0]!],
  3: [defaultRoutes[1]!],
};
