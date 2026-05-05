<template>
  <q-page class="compra-page">

    <PageHeader title="Bilheteira" :show-back="true" />

    <!-- pesquisa fixa + lista scrollable -->
    <template v-if="tipo === 'bilhete'">
      <div class="search-bar">
        <q-input
          v-model="searchCarreira"
          outlined clearable
          placeholder="Pesquisar carreira..."
          class="search-input"
        >
          <template #prepend><q-icon name="search" color="primary" /></template>
        </q-input>
      </div>

      <div class="carreira-scroll">
        <div v-if="carreirasLoading" class="text-center q-py-lg">
          <q-spinner color="blue-6" size="32px" />
        </div>
        <q-banner v-else-if="carreirasError" rounded dense class="bg-orange-1 text-orange-9">
          {{ carreirasError }}
          <template #action>
            <q-btn flat dense label="Tentar novamente" @click="loadCarreiras" />
          </template>
        </q-banner>
        <template v-else>
          <div
            v-for="c in carreirasFiltradas" :key="c.id"
            class="carreira-item"
            :class="{ 'carreira-item--selected': cart.carreiraID === c.id }"
            @click="pickCarreira(c)"
          >
            <q-icon
              name="directions_bus"
              :color="cart.carreiraID === c.id ? 'blue-7' : 'grey-5'"
              size="18px"
            />
            <span class="carreira-nome">{{ c.num }} — {{ c.nome }}</span>
            <q-icon v-if="cart.carreiraID === c.id" name="check_circle" color="blue-7" size="18px" />
          </div>
          <div v-if="carreirasFiltradas.length === 0" class="empty-state">
            <p>Nenhuma carreira encontrada.</p>
          </div>
        </template>
      </div>
    </template>

    <div v-else class="scroll-content">
      <template v-if="tipo === 'passe'">
        <p class="tipo-desc">Válido por 12 meses. Seleciona as zonas geográficas que queres cobrir:</p>
        <div class="field-label q-mb-xs">ZONAS</div>
        <ZonaPicker
          :zonas="zonas"
          :selected-ids="cart.zonaIDs"
          :loading="zonasLoading"
          :error="zonasError"
          @toggle="toggleZona"
          @retry="loadZonas"
        />
      </template>

      <template v-else-if="tipo === 'pacote'">
        <p class="tipo-desc">Escolhe o número de viagens e as zonas a cobrir:</p>
        <div class="field-label q-mb-xs">Nº DE VIAGENS</div>
        <div class="num-viagens-row">
          <q-btn flat round icon="remove" :disable="cart.numViagens <= 1" @click="cart.numViagens--" />
          <span class="num-viagens-val">{{ cart.numViagens }}</span>
          <q-btn flat round icon="add" @click="cart.numViagens++" />
        </div>
        <div class="field-label q-mt-lg q-mb-xs">ZONAS</div>
        <ZonaPicker
          :zonas="zonas"
          :selected-ids="cart.zonaIDs"
          :loading="zonasLoading"
          :error="zonasError"
          @toggle="toggleZona"
          @retry="loadZonas"
        />
      </template>
    </div>

    <div class="bottom-bar">
      <div class="total-row">
        <span class="total-label">Total a pagar:</span>
        <span class="total-chip" :class="{ 'total-chip--dim': !canProceed }">
          {{ canProceed ? formatPrice(total) : '—' }}
        </span>
      </div>
      <q-btn
        unelevated
        class="et-btn-primary full-width q-mt-sm"
        label="Avançar para Compra"
        :disable="!canProceed"
        @click="avancar"
      />
    </div>

  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import PageHeader from 'components/PageHeader.vue';
import ZonaPicker from 'components/ZonaPicker.vue';
import type { ZonaDTO, CarreiraDTO } from 'components/models';
import { zonesService } from 'src/services/zonesService';
import { routesService } from 'src/services/routesService';
import { useCartStore, type TipoTitulo } from 'stores/cart';

const PRECO_BILHETE       = 1.20;
const PRECO_PASSE_ZONA    = 15.00;
const PRECO_PACOTE_VIAGEM = 0.90;

const route  = useRoute();
const router = useRouter();
const cart   = useCartStore();

const tipo = computed(() => route.params.tipo as TipoTitulo);

const zonas        = ref<ZonaDTO[]>([]);
const zonasLoading = ref(true);
const zonasError   = ref('');

const carreiras        = ref<CarreiraDTO[]>([]);
const carreirasLoading = ref(false);
const carreirasError   = ref('');
const searchCarreira   = ref('');

const carreirasFiltradas = computed(() => {
  const q = searchCarreira.value.trim().toLowerCase();
  if (!q) return carreiras.value;
  return carreiras.value.filter(
    (c) => c.num.toLowerCase().includes(q) || c.nome.toLowerCase().includes(q),
  );
});

const canProceed = computed(() => {
  if (tipo.value === 'bilhete') return cart.carreiraID !== null;
  return cart.zonaIDs.length > 0;
});

const total = computed(() => {
  if (tipo.value === 'bilhete') return PRECO_BILHETE;
  if (tipo.value === 'passe')   return PRECO_PASSE_ZONA * cart.zonaIDs.length;
  return PRECO_PACOTE_VIAGEM * cart.numViagens;
});

function formatPrice(v: number) {
  return v.toFixed(2).replace('.', ',') + '€';
}

onMounted(() => {
  cart.reset();
  cart.tipo = tipo.value;
  if (tipo.value !== 'bilhete') void loadZonas();
  else void loadCarreiras();
});

async function loadZonas() {
  zonasError.value = '';
  try   { zonas.value = await zonesService.getAll(); }
  catch (e: unknown) {
    const err = e as { message?: string };
    zonasError.value = `Ocorreu um erro ao carregar zonas (${err?.message ?? 'sem resposta'})`;
  } finally { zonasLoading.value = false; }
}

async function loadCarreiras() {
  carreirasError.value   = '';
  carreirasLoading.value = true;
  try   { carreiras.value = await routesService.getAll(); }
  catch (e: unknown) {
    const err = e as { message?: string };
    carreirasError.value = `Ocorreu um erro ao carregar carreiras (${err?.message ?? 'sem resposta'})`;
  } finally { carreirasLoading.value = false; }
}

function pickCarreira(c: CarreiraDTO) {
  cart.carreiraID   = c.id;
  cart.carreiraNome = c.nome;
  cart.carreiraNum  = c.num;
}

function toggleZona(id: number) {
  const idx = cart.zonaIDs.indexOf(id);
  if (idx === -1) {
    cart.zonaIDs.push(id);
    const nome = zonas.value.find((z) => z.id === id)?.nome ?? '';
    cart.zonaNomes.push(nome);
  } else {
    cart.zonaIDs.splice(idx, 1);
    cart.zonaNomes.splice(idx, 1);
  }
}

function avancar() {
  void router.push('/app/bilhetes/checkout');
}
</script>

<style scoped>
.compra-page {
  background: var(--et-bg);
  display: flex;
  flex-direction: column;
  height: calc(100dvh - 60px);
  overflow: hidden;
}

.search-bar {
  flex-shrink: 0;
  padding: 12px 16px 8px;
}
.search-input :deep(.q-field__control) {
  border-radius: 20px;
  border-color: #719BF5;
  background: white;
}
.search-input :deep(.q-field__control:before) {
  border: 1px solid #719BF5 !important;
  border-radius: 20px;
}

.carreira-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 0 16px;
  padding-bottom: 130px; /* espaço para a barra fixa */
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-height: 0;
}

.scroll-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  padding-bottom: 130px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 0;
}

.tipo-desc { font-size: 14px; color: var(--et-subtext); margin: 0; }

.num-viagens-row { display: flex; align-items: center; gap: 16px; }
.num-viagens-val {
  font-size: 24px; font-weight: 700; color: var(--et-text);
  min-width: 32px; text-align: center;
}

.carreira-item {
  background: white; border-radius: 12px; padding: 14px 16px;
  display: flex; align-items: center; gap: 12px;
  cursor: pointer; border: 1.5px solid transparent;
  transition: border-color 0.15s, background 0.15s;
  flex-shrink: 0;
}
.carreira-item:active { background: #f9fafb; }
.carreira-item--selected { border-color: #719BF5; background: #eff6ff; }
.carreira-nome { flex: 1; font-size: 14px; font-weight: 500; color: var(--et-text); }

.bottom-bar {
  position: fixed;
  bottom: 60px;
  left: max(0px, calc(50vw - 215px));
  width: min(430px, 100vw);
  background: white;
  padding: 12px 16px 16px;
  border-top: 1px solid var(--et-border);
  z-index: 10;
}
.total-row   { display: flex; align-items: center; justify-content: space-between; }
.total-label { font-size: 15px; font-weight: 500; color: var(--et-text); }
.total-chip  {
  background: #f3f4f6; border-radius: 20px; padding: 6px 18px;
  font-size: 18px; font-weight: 700; color: var(--et-text);
}
.total-chip--dim { color: #9ca3af; }

.bottom-bar :deep(.q-btn__content) {
  font-family: 'Roboto', sans-serif !important;
  font-size: 13px !important;
  font-weight: 700 !important;
  letter-spacing: 0.5px !important;
}
</style>