<template>
  <q-page class="meus-bilhetes-page">

    <PageHeader title="Os meus bilhetes" :show-back="true" />

    <div class="page-content">
      <!-- pill tabs -->
      <div class="tabs-row">
        <button :class="['tab-pill', { 'tab-pill--active': tabAtiva === 'ativos' }]" @click="tabAtiva = 'ativos'">
          Disponíveis
        </button>
        <button :class="['tab-pill', { 'tab-pill--active': tabAtiva === 'utilizados' }]" @click="tabAtiva = 'utilizados'">
          Utilizados
        </button>
      </div>

      <div v-if="titulosLoading" class="q-mb-md">
        <div v-for="i in 2" :key="i" class="titulo-card titulo-card--skeleton">
          <q-skeleton type="text" width="40%" class="q-mb-sm" />
          <q-skeleton type="text" width="60%" />
        </div>
      </div>

      <div v-else-if="titulosFiltrados.length === 0" class="empty-state">
        <q-icon name="credit_card_off" size="32px" color="grey-4" />
        <p>{{ tabAtiva === 'ativos' ? 'Nenhum título disponível...' : 'Nenhum título utilizado...' }}</p>
      </div>

      <template v-else>
        <div v-for="t in titulosFiltrados" :key="t.id" class="titulo-card">
          <div class="titulo-card-header">
            <div class="titulo-card-header-left">
              <q-icon :name="tipoIcon(t.tipo)" size="18px" color="white" />
              <span class="titulo-card-nome">{{ tipoLabel(t.tipo) }}</span>
            </div>
            <span v-if="t.dataFim" class="titulo-card-data">{{ formatDate(t.dataFim) }}</span>
          </div>
          <div class="titulo-card-body">
            <div class="titulo-zonas-row">
              <ZoneBadge v-for="zona in t.zonas" :key="zona" :zona="zona" />
            </div>
            <div v-if="t.saldoRestante != null" class="titulo-saldo">
              <q-icon name="directions_bus" size="16px" color="grey-5" />
              <span>{{ t.saldoRestante }} viagem(ns) restante(s)</span>
            </div>
            <div v-if="tabAtiva === 'utilizados'" class="titulo-id">
            </div>
          </div>
        </div>
      </template>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import PageHeader from 'components/PageHeader.vue';
import ZoneBadge from 'components/ZoneBadge.vue';
import type { TituloDTO } from 'components/models';
import { formatDate } from 'src/utils/formatters';
import { tipoIcon } from 'src/utils/titulo';
import { titlesService } from 'src/services/titlesService';

const titulos        = ref<TituloDTO[]>([]);
const titulosLoading = ref(true);
const tabAtiva       = ref<'ativos' | 'utilizados'>('ativos');

const titulosFiltrados = computed(() =>
  titulos.value.filter((t) => tabAtiva.value === 'ativos' ? t.valido : !t.valido),
);

function tipoLabel(tipo: string) {
  return tipo?.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, (c) => c.toUpperCase()) ?? '';
}

onMounted(() => { void loadTitulos(); });

async function loadTitulos() {
  try   { titulos.value = await titlesService.getAll(); }
  finally { titulosLoading.value = false; }
}
</script>

<style scoped>
.meus-bilhetes-page { background: var(--et-bg); min-height: 100dvh; }

/* pill tabs */
.tabs-row { display: flex; gap: 8px; margin-bottom: 16px; }
.tab-pill {
  padding: 7px 22px; border-radius: 20px; cursor: pointer; font-size: 13px; font-weight: 600;
  background: transparent; color: #364C7B; border: 2px solid #364C7B;
  transition: background 0.15s, color 0.15s;
}
.tab-pill--active { background: #364C7B; color: white; border-color: #364C7B; }

/* cards */
.titulo-card {
  background: white; border-radius: 14px; margin-bottom: 10px;
  overflow: hidden; box-shadow: 0 1px 4px rgba(0,0,0,0.07);
}
.titulo-card--skeleton { padding: 16px; }

.titulo-card-header {
  background: #719BF5; padding: 10px 14px;
  display: flex; align-items: center; justify-content: space-between;
}
.titulo-card-header-left { display: flex; align-items: center; gap: 6px; }
.titulo-card-nome { font-size: 13px; font-weight: 700; color: white; letter-spacing: 0.3px; }
.titulo-card-data { font-size: 11px; color: rgba(255,255,255,0.85); font-weight: 500; }

.titulo-card-body { padding: 12px 14px; display: flex; flex-direction: column; gap: 6px; }
.titulo-zonas-row { display: flex; flex-wrap: wrap; gap: 4px; }

.titulo-saldo { display: flex; align-items: center; gap: 6px; font-size: 12px; color: #6b7280; }
.titulo-id    { font-size: 11px; color: #9ca3af; }

.empty-state { text-align: center; padding: 40px 0; color: var(--et-subtext); font-size: 14px; }
.empty-state p { margin: 8px 0 0; }
</style>
