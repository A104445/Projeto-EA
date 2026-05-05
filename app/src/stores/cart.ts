import { defineStore } from 'pinia';
import { ref } from 'vue';

export type TipoTitulo = 'bilhete' | 'passe' | 'pacote';

export const useCartStore = defineStore('cart', () => {
  const tipo        = ref<TipoTitulo>('bilhete');
  const carreiraID  = ref<number | null>(null);
  const carreiraNome = ref('');
  const carreiraNum  = ref('');
  const quantidade  = ref(1);
  const zonaIDs     = ref<number[]>([]);
  const zonaNomes   = ref<string[]>([]);
  const numViagens  = ref(5);

  function reset() {
    carreiraID.value   = null;
    carreiraNome.value = '';
    carreiraNum.value  = '';
    quantidade.value   = 1;
    zonaIDs.value      = [];
    zonaNomes.value    = [];
    numViagens.value   = 5;
  }

  return { tipo, carreiraID, carreiraNome, carreiraNum, quantidade, zonaIDs, zonaNomes, numViagens, reset };
});