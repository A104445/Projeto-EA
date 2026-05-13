<template>
  <div v-if="loading" class="text-grey-5 text-caption">A carregar zonas...</div>
  <q-banner v-else-if="error" rounded dense class="bg-orange-1 text-orange-9 q-mb-sm">
    {{ error }}
    <template #action>
      <q-btn flat dense label="Tentar novamente" @click="$emit('retry')" />
    </template>
  </q-banner>
  <div v-else class="zonas-chips">
    <button
      v-for="z in zonas" :key="z.id"
      class="zona-chip"
      :class="{ 'zona-chip-selected': selectedIds.includes(z.id) }"
      :style="selectedIds.includes(z.id)
        ? { background: zonaColor(z.nome), borderColor: zonaColor(z.nome) }
        : { borderColor: zonaColor(z.nome), color: zonaColor(z.nome) }"
      @click="$emit('toggle', z.id)"
    >{{ zonaBadgeLabel(z.nome) }}</button>
  </div>
</template>

<script setup lang="ts">
import type { ZonaDTO } from 'components/models';
import { zonaColor, zonaBadgeLabel } from 'src/utils/zones';

defineProps<{
  zonas: ZonaDTO[];
  selectedIds: number[];
  loading: boolean;
  error: string;
}>();
defineEmits<{ toggle: [id: number]; retry: [] }>();
</script>

<style scoped>
.zonas-chips { display: flex; flex-wrap: wrap; gap: 8px; }
.zona-chip {
  padding: 8px 18px; border-radius: 24px; border: 2px solid;
  background: transparent; font-size: 13px; font-weight: 600;
  cursor: pointer; transition: background 0.15s, color 0.15s; outline: none;
  scroll-margin-bottom: 180px;
}
.zona-chip-selected { color: white !important; }
</style>