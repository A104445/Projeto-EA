<template>
  <q-page class="percursos-page">
    <div class="page-header">
      <h1 class="page-title">Histórico de Viagens</h1>
    </div>

    <!-- filtros -->
    <div class="filters-row">
      <button class="filter-chip" :class="{ active: filter === 'todas' }" @click="filter = 'todas'">Todas</button>
      <button class="filter-chip" :class="{ active: filter === 'irregulares' }" @click="filter = 'irregulares'">Irregulares</button>
    </div>

    <div class="page-content">
      <template v-if="loading">
        <div v-for="i in 4" :key="i" class="trip-card">
          <q-skeleton type="circle" size="44px" />
          <div class="trip-card-body" style="flex:1">
            <q-skeleton type="text" width="55%" />
            <q-skeleton type="text" width="75%" style="margin-top:4px" />
            <q-skeleton type="text" width="40%" style="margin-top:4px" />
          </div>
        </div>
      </template>

      <div v-else-if="filtered.length === 0" class="empty-state">
        <q-icon name="route" size="48px" color="grey-4" />
        <p>{{ filter === 'irregulares' ? 'Sem viagens irregulares.' : 'Ainda não tens viagens.' }}</p>
      </div>

      <template v-else>
        <div v-for="v in filtered" :key="v.ID" class="trip-card">
          <div class="trip-icon-wrap" :class="iconClass(v)">
            <q-icon :name="tripIconFromNome(v.carreiraNome)" size="20px" color="white" />
          </div>
          <div class="trip-card-body">
            <div class="trip-card-top">
              <span class="trip-carreira">{{ v.carreiraNum }} — {{ v.carreiraNome }}</span>
              <span v-if="v.irregular" class="badge-irregular">Irregular</span>
              <span v-else-if="v.estado === 'EM_CURSO'" class="badge-em-curso">Em curso</span>
            </div>
            <p class="trip-route">
              {{ v.origemNome }}
              <q-icon name="arrow_forward" size="12px" style="margin: 0 2px; vertical-align: middle;" />
              {{ v.destinoNome ?? '—' }}
            </p>
            <div class="trip-meta">
              <span>{{ formatDateTime(v.dataHoraEntrada) }}</span>
              <span v-if="v.dataHoraSaida" class="trip-duration">· {{ duration(v.dataHoraEntrada, v.dataHoraSaida) }}</span>
            </div>
          </div>
        </div>
      </template>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import type { ViagemDTO } from 'components/models';
import { formatDateTime, duration } from 'src/utils/formatters';
import { tripIconFromNome } from 'src/utils/titulo';
import { tripsService } from 'src/services/tripsService';

const viagens = ref<ViagemDTO[]>([]);
const loading = ref(true);
const filter  = ref<'todas' | 'irregulares'>('todas');

const filtered = computed(() => {
  const list = viagens.value.filter((v) => v.estado !== 'EM_CURSO');
  if (filter.value === 'irregulares') return list.filter((v) => v.irregular);
  return list;
});

onMounted(async () => {
  try   { viagens.value = await tripsService.getHistory(); }
  finally { loading.value = false; }
});

function iconClass(v: ViagemDTO): string {
  if (v.irregular)           return 'icon-irregular';
  if (v.estado === 'EM_CURSO') return 'icon-em-curso';
  return 'icon-normal';
}
</script>

<style scoped>
.percursos-page { background: var(--et-bg); min-height: 100dvh; }

.filters-row {
  background: var(--et-blue); padding: 0 16px 16px; display: flex; gap: 8px;
}
.filter-chip {
  padding: 6px 16px; border-radius: 20px; border: 1.5px solid rgba(255,255,255,0.4);
  background: transparent; color: rgba(255,255,255,0.75); font-size: 13px; font-weight: 600;
  cursor: pointer; transition: background 0.15s, color 0.15s, border-color 0.15s;
}
.filter-chip.active { background: white; color: var(--et-blue); border-color: white; }

.trip-card {
  background: white; border-radius: 14px; padding: 14px 16px;
  display: flex; align-items: flex-start; gap: 14px;
}
.trip-icon-wrap {
  width: 44px; height: 44px; border-radius: 12px; flex-shrink: 0;
  display: flex; align-items: center; justify-content: center;
}
.icon-normal    { background: #3b82f6; }
.icon-irregular { background: #f97316; }
.icon-em-curso  { background: #16a34a; }

.trip-card-body { flex: 1; min-width: 0; }
.trip-card-top  { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; margin-bottom: 4px; }
.trip-carreira  { font-size: 14px; font-weight: 700; color: var(--et-text); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.badge-irregular { font-size: 10px; font-weight: 700; color: white; background: #f97316; border-radius: 20px; padding: 2px 8px; flex-shrink: 0; }
.badge-em-curso  { font-size: 10px; font-weight: 700; color: white; background: #16a34a; border-radius: 20px; padding: 2px 8px; flex-shrink: 0; }

.trip-route  { font-size: 13px; color: var(--et-subtext); margin: 0 0 4px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.trip-meta   { font-size: 12px; color: #9ca3af; display: flex; gap: 4px; }
.trip-duration { color: #9ca3af; }
</style>