/** formata uma data para "DD MMM AAAA" em PT */
export function formatDate(dateStr?: string): string {
  if (!dateStr) return '—';
  return new Date(dateStr)
    .toLocaleDateString('pt-PT', { day: '2-digit', month: 'short', year: 'numeric' })
    .toUpperCase();
}

/** formata uma data/hora para "Hoje, HH:MM" / "Ontem, HH:MM" / "DD MMM, HH:MM" */
export function formatDateTime(dateStr?: string): string {
  if (!dateStr) return '';
  const d         = new Date(dateStr);
  const today     = new Date();
  const yesterday = new Date(today);
  yesterday.setDate(today.getDate() - 1);
  const time = d.toLocaleTimeString('pt-PT', { hour: '2-digit', minute: '2-digit' });
  if (d.toDateString() === today.toDateString())     return `Hoje, ${time}`;
  if (d.toDateString() === yesterday.toDateString()) return `Ontem, ${time}`;
  return d.toLocaleDateString('pt-PT', { day: '2-digit', month: 'short' }) + ', ' + time;
}

/** calcula a duração entre dois ISO strings → "X min" ou "Xh Ymin" */
export function duration(start: string, end: string): string {
  const mins = Math.round((new Date(end).getTime() - new Date(start).getTime()) / 60000);
  if (mins < 60) return `${mins} min`;
  const h = Math.floor(mins / 60);
  const m = mins % 60;
  return m > 0 ? `${h}h ${m}min` : `${h}h`;
}