/** icon correspondente ao tipo de título */
export function tipoIcon(tipo: string): string {
  const t = (tipo ?? '').toLowerCase();
  if (t.includes('passe'))   return 'card_membership';
  if (t.includes('bilhete')) return 'confirmation_number';
  if (t.includes('pacote'))  return 'local_activity';
  return 'credit_card';
}

/** cor correspondente ao tipo de título */
export function tipoColor(tipo: string): string {
  const t = (tipo ?? '').toLowerCase();
  if (t.includes('passe'))   return 'blue-7';
  if (t.includes('bilhete')) return 'orange-7';
  if (t.includes('pacote'))  return 'purple-7';
  return 'grey-7';
}

/** icon deduzido do nome da carreira */
export function tripIconFromNome(nome: string): string {
  const n = (nome ?? '').toLowerCase();
  if (n.includes('metro'))                          return 'subway';
  if (n.includes('comboio') || n.includes('cp'))   return 'train';
  if (n.includes('ferry')   || n.includes('barco')) return 'directions_boat';
  return 'directions_bus';
}