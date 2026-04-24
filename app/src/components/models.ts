export interface Todo {
  id: number;
  content: string;
}

export interface Meta {
  totalCount: number;
}

export interface User {
  ID: number;
  nome: string;
  email: string;
  dataNascimento?: string;
  telefone?: string;
}

export interface ZonaDTO {
  ID: number;
  nome: string;
}

export interface ParagemDTO {
  ID: number;
  nome: string;
  qr: string;
}

export interface VeiculoDTO {
  ID: number;
  matricula: string;
  capacidade: number;
  ocupacaoAtual: number;
}

export interface CarreiraDTO {
  ID: number;
  num: string;
  nome: string;
  paragens?: ParagemDTO[];
  proximaChegada?: string; // "HH:mm" — presente quando ordenado por horário
  veiculoProximo?: VeiculoDTO; // presente no contexto de check-in
}

export interface HorarioDTO {
  ID: number;
  carreiraID: number;
  paragemID: number;
  paragemNome: string;
  horaChegada: string; // "HH:mm:ss"
  diaSemana: number;
}

export interface TituloDTO {
  ID: number;
  tipo: string;
  estado: string;
  valido: boolean;
  zonas: string[];
  dataFim?: string;
  saldoRestante?: number;
}

export interface ViagemDTO {
  ID: number;
  estado: string;
  dataHoraEntrada: string;
  dataHoraSaida?: string;
  carreiraNum: string;
  carreiraNome: string;
  origemNome: string;
  destinoNome?: string;
  irregular?: boolean;
}

export interface NotificacaoDTO {
  tipo: 'PASSE_EXPIRANDO' | 'PASSE_EXPIRADO' | 'SALDO_BAIXO' | 'VIAGEM_IRREGULAR';
  mensagem: string;
  tituloID: number;
}
