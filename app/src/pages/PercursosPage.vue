<template>
  <q-page class="percursos-page">
    <PageHeader title="Histórico de Viagens" />

    <div class="page-content">
      <!-- barra de pesquisa + filtrar -->
      <div class="search-row">
        <q-input
          v-model="searchText"
          outlined dense clearable placeholder="Pesquisar linha..."
          class="search-input"
          bg-color="white"
        >
          <template #prepend><q-icon name="search" color="grey-5" /></template>
        </q-input>
        <button class="filter-btn" :class="{ 'filter-btn--active': hasActiveFilters }" @click="filterDialog = true">
          <q-icon name="tune" size="18px" />
          <span>Filtrar</span>
          <span v-if="activeFilterCount > 0" class="filter-badge">{{ activeFilterCount }}</span>
        </button>
      </div>

      <!-- chips de filtros ativos -->
      <div v-if="hasActiveFilters" class="active-chips">
        <span v-if="filterStatus === 'irregulares'" class="active-chip">
          Irregulares
          <q-icon name="close" size="12px" class="chip-close" @click="filterStatus = 'todas'" />
        </span>
        <span v-if="filterData" class="active-chip">
          {{ filterData }}
          <q-icon name="close" size="12px" class="chip-close" @click="filterData = ''" />
        </span>
        <button class="clear-all" @click="clearFilters">Limpar tudo</button>
      </div>

      <!-- lista -->
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
        <p>{{ hasActiveFilters ? 'Nenhuma viagem corresponde aos filtros.' : 'Ainda não tens viagens.' }}</p>
        <button v-if="hasActiveFilters" class="clear-all-empty" @click="clearFilters">Limpar filtros</button>
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

    <!-- ── filter sheet ── -->
    <q-dialog v-model="filterDialog" position="bottom">
      <q-card class="bottom-sheet">
        <q-card-section class="sheet-header">
          <span class="sheet-title">Filtrar viagens</span>
          <q-btn flat round dense icon="close" @click="filterDialog = false" />
        </q-card-section>

        <q-card-section class="sheet-body">
          <!-- estado -->
          <div class="field-label q-mb-sm">ESTADO</div>
          <div class="status-chips">
            <button
              v-for="opt in statusOpts" :key="opt.value"
              class="status-chip"
              :class="{ 'status-chip--active': filterStatus === opt.value }"
              @click="filterStatus = opt.value"
            >{{ opt.label }}</button>
          </div>

          <!-- data -->
          <div class="field-label q-mt-md q-mb-sm">DATA</div>
          <q-input
            v-model="filterData"
            outlined dense clearable
            type="date"
            bg-color="white"
          />
        </q-card-section>

        <q-card-actions class="sheet-actions">
          <q-btn flat label="Limpar" color="grey-6" @click="clearFilters" />
          <q-btn unelevated class="et-btn-primary" label="Aplicar" @click="filterDialog = false" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import PageHeader from 'components/PageHeader.vue';
import type { ViagemDTO } from 'components/models';
import { formatDateTime, duration } from 'src/utils/formatters';
import { tripIconFromNome } from 'src/utils/titulo';
import { tripsService } from 'src/services/tripsService';

const viagens = ref<ViagemDTO[]>([]);
const loading = ref(true);

const filterDialog = ref(false);
const searchText   = ref('');
const filterStatus = ref<'todas' | 'irregulares'>('todas');
const filterData   = ref('');

const statusOpts = [
  { value: 'todas',       label: 'Todas' },
  { value: 'irregulares', label: 'Irregulares' },
] as const;

const activeFilterCount = computed(() => {
  let n = 0;
  if (filterStatus.value !== 'todas') n++;
  if (filterData.value) n++;
  return n;
});
const hasActiveFilters = computed(() => activeFilterCount.value > 0 || searchText.value.trim() !== '');

const filtered = computed(() => {
  let list = viagens.value.filter((v) => v.estado !== 'EM_CURSO');

  if (filterStatus.value === 'irregulares') list = list.filter((v) => v.irregular);

  if (filterData.value) {
    list = list.filter((v) => v.dataHoraEntrada?.startsWith(filterData.value));
  }

  const q = searchText.value.trim().toLowerCase();
  if (q) {
    list = list.filter(
      (v) =>
        v.carreiraNum?.toLowerCase().includes(q) ||
        v.carreiraNome?.toLowerCase().includes(q),
    );
  }

  return list;
});

onMounted(async () => {
  try   { viagens.value = await tripsService.getHistory(); }
  finally { loading.value = false; }
});

function iconClass(v: ViagemDTO): string {
  if (v.irregular)            return 'icon-irregular';
  if (v.estado === 'EM_CURSO') return 'icon-em-curso';
  return 'icon-normal';
}

function clearFilters() {
  filterStatus.value = 'todas';
  filterData.value   = '';
  searchText.value   = '';
  filterDialog.value = false;
}
</script>

<style scoped>
.percursos-page { background: var(--et-bg); min-height: 100dvh; }

/* search + filter row */
.search-row  { display: flex; gap: 10px; align-items: center; }
.search-input { flex: 1; }

.filter-btn {
  display: flex; align-items: center; gap: 6px;
  padding: 0 14px; height: 40px; border-radius: 10px;
  border: 1.5px solid var(--et-border); background: white;
  font-size: 13px; font-weight: 600; color: var(--et-text);
  cursor: pointer; white-space: nowrap; flex-shrink: 0;
  transition: border-color 0.15s, background 0.15s;
}
.filter-btn--active { border-color: var(--et-blue); color: var(--et-blue); background: #eff6ff; }
.filter-badge {
  background: var(--et-blue); color: white;
  font-size: 10px; font-weight: 700;
  width: 18px; height: 18px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
}

/* active chips */
.active-chips { display: flex; flex-wrap: wrap; gap: 8px; align-items: center; }
.active-chip {
  display: flex; align-items: center; gap: 5px;
  background: #eff6ff; color: var(--et-blue);
  border-radius: 20px; padding: 4px 10px;
  font-size: 12px; font-weight: 600;
}
.chip-close { cursor: pointer; opacity: 0.7; }
.chip-close:hover { opacity: 1; }
.clear-all {
  background: none; border: none; font-size: 12px; color: var(--et-subtext);
  cursor: pointer; text-decoration: underline; padding: 0;
}
.clear-all-empty {
  margin-top: 10px; background: none; border: 1px solid var(--et-border);
  border-radius: 8px; padding: 6px 16px; font-size: 13px; color: var(--et-subtext);
  cursor: pointer;
}

/* status chips inside sheet */
.status-chips { display: flex; gap: 8px; }
.status-chip {
  padding: 7px 18px; border-radius: 20px; border: 1.5px solid var(--et-border);
  background: white; font-size: 13px; font-weight: 600; color: var(--et-text);
  cursor: pointer; transition: background 0.15s, border-color 0.15s, color 0.15s;
}
.status-chip--active { background: var(--et-blue); color: white; border-color: var(--et-blue); }

/* trip cards */
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
