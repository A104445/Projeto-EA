<template>
  <q-page class="percursos-page">
    <PageHeader title="Percursos e Horários" />

    <div class="percursos-content">

      <q-input
        v-model="search"
        outlined
        clearable
        placeholder="Procurar linha..."
        class="search-input"
      >
        <template #prepend>
          <q-icon name="directions_bus" color="primary" size="20px" />
        </template>
      </q-input>

      <div v-if="loading" class="text-center q-py-lg">
        <q-spinner color="blue-6" size="32px" />
      </div>

      <template v-else>
        <div
          v-for="c in carreirasFiltradas"
          :key="c.id"
          class="linha-card"
        >
          <span class="linha-nome">{{ c.nome }}</span>
          <q-btn flat round dense icon="schedule" color="blue-7" @click="verDetalhe(c)" />
        </div>
        <div v-if="carreirasFiltradas.length === 0" class="empty-state">
          <p>Nenhuma carreira encontrada.</p>
        </div>
      </template>
    </div>

    <!-- sheet de horários -->
    <q-dialog v-model="horarioDialog" position="bottom" @hide="resetHorario">
      <q-card class="bottom-sheet horarios-sheet">

        <q-card-section class="sheet-header">
          <span class="sheet-title">Horário — {{ carreiraLabel }}</span>
          <q-btn flat round dense icon="close" @click="horarioDialog = false" />
        </q-card-section>

        <!-- seletor de dia da semana -->
        <q-card-section class="day-selector-section">
          <div class="day-chips">
            <button
              v-for="d in DAYS"
              :key="d.value"
              :class="['day-chip', selectedDay === d.value ? 'day-chip--active' : '']"
              @click="selectDay(d.value)"
            >{{ d.label }}</button>
          </div>
        </q-card-section>

        <q-card-section class="sheet-body">
          <div v-if="horariosLoading" class="text-center q-py-lg">
            <q-spinner color="blue-6" size="32px" />
          </div>
          <div v-else-if="trips.length === 0" class="empty-state">
            <p>Sem horários para este dia.</p>
          </div>
          <div v-else class="trips-list">
            <div v-for="(trip, idx) in trips" :key="idx" class="trip-card">
              <div class="trip-diagram">
                <template v-for="(stop, si) in trip" :key="si">
                  <!-- linha de conteúdo: hora | dot | nome alinhados ao centro -->
                  <div class="trip-stop">
                    <div class="stop-time">{{ stop.time ?? '—' }}</div>
                    <div class="stop-dot-wrap"><div class="stop-dot" /></div>
                    <div class="stop-name">{{ stop.paragemNome }}</div>
                  </div>
                  <!-- linha conectora entre paragens -->
                  <div v-if="si < trip.length - 1" class="trip-connector">
                    <div class="stop-vert" />
                  </div>
                </template>
              </div>
            </div>
          </div>
        </q-card-section>

      </q-card>
    </q-dialog>

  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import PageHeader from 'components/PageHeader.vue';
import type { CarreiraDTO, HorarioDTO } from 'components/models';
import { routesService } from 'src/services/routesService';

const DAYS = [
  { value: 1, label: 'seg.' },
  { value: 2, label: 'ter.' },
  { value: 3, label: 'qua.' },
  { value: 4, label: 'qui.' },
  { value: 5, label: 'sex.' },
  { value: 6, label: 'sáb.' },
  { value: 7, label: 'dom.' },
];

const carreiras     = ref<CarreiraDTO[]>([]);
const loading       = ref(true);
const search        = ref('');

const horarioDialog    = ref(false);
const carreiraLabel    = ref('');
const horarios         = ref<HorarioDTO[]>([]);
const horariosLoading  = ref(false);
const selectedDay      = ref(1);
const selectedCarreira = ref<CarreiraDTO | null>(null);

const carreirasFiltradas = computed(() => {
  const q = search.value.trim().toLowerCase();
  if (!q) return carreiras.value;
  return carreiras.value.filter(
    (c) => c.num.toLowerCase().includes(q) || c.nome.toLowerCase().includes(q),
  );
});

const trips = computed(() => {
  const stopOrder: number[] = [];
  const stopNames = new Map<number, string>();
  const stopTimes = new Map<number, string[]>();

  for (const h of horarios.value) {
    if (!stopNames.has(h.paragemId)) {
      stopOrder.push(h.paragemId);
      stopNames.set(h.paragemId, h.paragemNome);
      stopTimes.set(h.paragemId, []);
    }
    stopTimes.get(h.paragemId)!.push(h.horaChegada.slice(0, 5));
  }

  for (const [id, times] of stopTimes) {
    stopTimes.set(id, [...times].sort());
  }

  const maxTrips = Math.max(0, ...Array.from(stopTimes.values()).map((t) => t.length));

  return Array.from({ length: maxTrips }, (_, i) =>
    stopOrder.map((id) => ({
      paragemId: id,
      paragemNome: stopNames.get(id)!,
      time: stopTimes.get(id)?.[i] ?? null,
    })),
  );
});

onMounted(async () => {
  try   { carreiras.value = await routesService.getAll(); }
  finally { loading.value = false; }
});

async function loadSchedules() {
  if (!selectedCarreira.value) return;
  horariosLoading.value = true;
  try {
    horarios.value = await routesService.getSchedules(selectedCarreira.value.id, selectedDay.value);
  } finally {
    horariosLoading.value = false;
  }
}

async function verDetalhe(c: CarreiraDTO) {
  carreiraLabel.value    = c.nome;
  selectedCarreira.value = c;
  horarioDialog.value    = true;
  const jsDay = new Date().getDay();
  selectedDay.value      = jsDay === 0 ? 7 : jsDay;
  await loadSchedules();
}

function selectDay(day: number) {
  selectedDay.value = day;
  void loadSchedules();
}

function resetHorario() {
  horarios.value         = [];
  carreiraLabel.value    = '';
  selectedCarreira.value = null;
}
</script>

<style scoped>
.percursos-page { background: #F6F6F6; min-height: 100dvh; }


.percursos-content {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.section-title {
  font-family: 'Roboto', sans-serif;
  font-weight: 700;
  font-size: 18px;
  color: var(--et-text);
  margin: 0;
  text-shadow: 0px 2px 20px rgba(0, 0, 0, 0.25);
}

/* ── campo de pesquisa ── */
.search-input :deep(.q-field__control) {
  border-radius: 12px;
  background: white;
}
.search-input :deep(.q-field__control:before) {
  border: 1px solid #719BF5 !important;
  border-radius: 12px;
}

/* ── cards de linha ── */
.linha-card {
  background: white;
  border-radius: 14px;
  padding: 14px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border: 1px solid var(--et-border);
  cursor: pointer;
  transition: background 0.15s;
}
.linha-card:active { background: #f9fafb; }
.linha-nome {
  font-size: 15px;
  font-weight: 700;
  color: var(--et-text);
}

/* ── sheet de horários ── */
.horarios-sheet {
  max-height: 88dvh;
  display: flex;
  flex-direction: column;
  border-radius: 20px 20px 0 0;
}


/* ── day selector ── */
.day-selector-section {
  padding-top: 0;
  padding-bottom: 12px;
}
.day-chips {
  display: flex;
  gap: 6px;
  overflow-x: auto;
  scrollbar-width: none;
}
.day-chips::-webkit-scrollbar { display: none; }
.day-chip {
  font-family: 'Jaldi', sans-serif;
  font-size: 13px;
  font-weight: 700;
  padding: 4px 12px;
  border-radius: 20px;
  border: 1.5px solid #719BF5;
  background: white;
  color: #719BF5;
  cursor: pointer;
  white-space: nowrap;
  flex-shrink: 0;
}
.day-chip--active {
  background: #719BF5;
  color: white;
}

.sheet-body {
  flex: 1;
  overflow-y: auto;
}

.trips-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}
.trip-card {
  background: white;
  border-radius: 12px;
  padding: 12px 8px;
}
.trip-diagram {
  display: flex;
  flex-direction: column;
}
.trip-stop {
  display: grid;
  grid-template-columns: 1fr 14px 1fr;
  align-items: center;
  column-gap: 4px;
}
.stop-time {
  font-family: 'Roboto', sans-serif;
  font-weight: 700;
  font-size: 13px;
  color: var(--et-text);
  text-align: right;
  line-height: 1;
}
.stop-dot-wrap {
  display: flex;
  justify-content: center;
}
.stop-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #719BF5;
  flex-shrink: 0;
}
.trip-connector {
  display: grid;
  grid-template-columns: 1fr 14px 1fr;
  column-gap: 4px;
  height: 14px;
}
.stop-vert {
  grid-column: 2;
  justify-self: center;
  width: 2px;
  height: 100%;
  background: #719BF5;
}
.stop-name {
  font-family: 'Jaldi', sans-serif;
  font-size: 11px;
  font-weight: 400;
  color: var(--et-text);
  line-height: 1;
}

</style>
