import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useNotificacoesStore = defineStore('notificacoes', () => {
  const count = ref(0);

  function clear() { count.value = 0; }
  function set(n: number) { count.value = n; }

  return { count, clear, set };
});
