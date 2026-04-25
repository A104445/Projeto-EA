import type {
  CarreiraDTO,
  HorarioDTO,
  NotificacaoDTO,
  TituloDTO,
  User,
  ViagemDTO,
  ZonaDTO,
} from '../../src/components/models';

export const defaultUser: User = {
  ID: 1,
  nome: 'Joao Silva',
  email: 'joao@example.com',
  telefone: '912345678',
  dataNascimento: '2000-01-15',
};

export const defaultActiveTitles: TituloDTO[] = [
  {
    ID: 101,
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
    ID: 102,
    tipo: 'PACOTE_VIAGENS',
    estado: 'ATIVO',
    valido: true,
    zonas: ['Zona C'],
    saldoRestante: 3,
  },
];

export const defaultZones: ZonaDTO[] = [
  { ID: 1, nome: 'Zona A' },
  { ID: 2, nome: 'Zona B' },
  { ID: 3, nome: 'Zona C' },
];

export const defaultRoutes: CarreiraDTO[] = [
  {
    ID: 201,
    num: '101',
    nome: 'Aeroporto',
    paragens: [],
    proximaChegada: '10:15',
  },
  {
    ID: 202,
    num: '202',
    nome: 'Universidade',
    paragens: [],
    proximaChegada: '10:25',
  },
];

export const defaultSchedulesByRoute: Record<number, HorarioDTO[]> = {
  201: [
    { ID: 1, carreiraID: 201, paragemID: 1, paragemNome: 'Terminal', horaChegada: '10:15:00', diaSemana: 6 },
    { ID: 2, carreiraID: 201, paragemID: 2, paragemNome: 'Campus', horaChegada: '10:40:00', diaSemana: 6 },
  ],
  202: [
    { ID: 3, carreiraID: 202, paragemID: 3, paragemNome: 'Centro', horaChegada: '11:00:00', diaSemana: 6 },
  ],
};

export const defaultHistoryTrips: ViagemDTO[] = [
  {
    ID: 301,
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
    ID: 302,
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
