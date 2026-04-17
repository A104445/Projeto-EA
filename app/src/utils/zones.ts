export const ZONE_COLORS: Record<string, string> = {
  A: '#3b82f6',
  B: '#22c55e',
  C: '#f97316',
  D: '#a855f7',
  E: '#ec4899',
};

/** devolve a cor para o nome de uma zona (ex: "Zona A" → #3b82f6) */
export function zonaColor(nome: string): string {
  const m = nome.match(/Zona\s+([A-Z])/i);
  return m?.[1] ? (ZONE_COLORS[m[1].toUpperCase()] ?? '#6b7280') : '#6b7280';
}

/** devolve o label curto de uma zona (ex: "Zona A") */
export function zonaBadgeLabel(nome: string): string {
  const m = nome.match(/Zona\s+([A-Z])/i);
  return m?.[1] ? `Zona ${m[1].toUpperCase()}` : nome;
}