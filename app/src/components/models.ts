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

export interface CarreiraDTO {
  ID: number;
  num: string;
  nome: string;
  paragens?: ParagemDTO[];
  proximaChegada?: string; // "HH:mm" — presente quando ordenado por horário
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
