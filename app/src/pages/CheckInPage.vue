<template>
  <q-page class="checkin-page">
    <PageHeader title="Fazer Check-in" :show-back="true" />

    <div class="page-content">
      <!-- PASSO 1: scan QR da paragem -->
      <template v-if="step === 1">
        <p class="step-label">PASSO 1 DE 2</p>
        <p class="step-title">Aponta a câmara ao QR da paragem.</p>
        <QrScanner @scan="onQrScanned" />
        <q-banner v-if="qrError" rounded dense class="bg-red-1 text-red-8 q-mt-md">
          {{ qrError }}
        </q-banner>
        <div v-if="qrLoading" class="text-center q-mt-md">
          <q-spinner color="blue-6" size="32px" />
          <p class="text-grey-6 q-mt-sm">A identificar paragem...</p>
        </div>
      </template>

      <!-- PASSO 2: confirmar carreira + título -->
      <template v-if="step === 2">
        <p class="step-label">PASSO 2 DE 2</p>
        <p class="step-title">Confirma os dados da viagem.</p>

        <div class="info-card">
          <div class="info-card-icon">
            <q-icon name="location_on" size="22px" color="blue-7" />
          </div>
          <div>
            <p class="info-card-label">PARAGEM DE ENTRADA</p>
            <p class="info-card-value">{{ paragem?.nome }}</p>
          </div>
          <q-btn flat round dense icon="qr_code_scanner" color="grey-5" @click="reset" />
        </div>

        <div class="field-label q-mt-md q-mb-xs">CARREIRA</div>
        <div v-if="carreirasLoading" class="text-grey-5 text-caption q-mb-md">A carregar carreiras...</div>
        <div v-else-if="carreiras.length === 0" class="empty-warning">
          Nenhuma carreira passa nesta paragem...
        </div>
        <q-list v-else separator class="select-list q-mb-md">
          <q-item
            v-for="c in carreiras" :key="c.id"
            clickable
            :class="{ 'item-selected': selectedCarreiraID === c.id }"
            @click="selectedCarreiraID = c.id"
          >
            <q-item-section avatar>
              <q-icon name="directions_bus" :color="selectedCarreiraID === c.id ? 'blue-7' : 'grey-5'" />
            </q-item-section>
            <q-item-section>
              <q-item-label>{{ c.nome }}</q-item-label>
              <q-item-label v-if="c.proximaChegada" caption>
                <q-icon name="schedule" size="11px" /> próxima: {{ c.proximaChegada }}
              </q-item-label>
            </q-item-section>
            <q-item-section side>
              <q-icon v-if="selectedCarreiraID === c.id" name="check_circle" color="blue-7" />
            </q-item-section>
          </q-item>
        </q-list>

        <div class="field-label q-mb-xs">TÍTULO A USAR</div>
        <div v-if="titulosLoading" class="text-grey-5 text-caption q-mb-md">A carregar títulos...</div>
        <div v-else-if="titulos.length === 0" class="empty-warning">
          Não tens títulos válidos!
          <q-btn flat no-caps dense label="Comprar título" color="blue-6"
                 @click="$router.push('/app/bilhetes')" />
        </div>
        <q-list v-else separator class="select-list q-mb-lg">
          <q-item
            v-for="t in titulosDisponiveis" :key="t.id"
            clickable
            :class="{ 'item-selected': selectedTituloID === t.id }"
            @click="selectedTituloID = t.id"
          >
            <q-item-section avatar>
              <q-icon :name="tipoIcon(t.tipo)" :color="selectedTituloID === t.id ? 'blue-7' : 'grey-5'" />
            </q-item-section>
            <q-item-section>
              <q-item-label>{{ t.tipo?.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase()) }}</q-item-label>
              <div class="titulo-zonas-row">
                <ZoneBadge v-for="zona in t.zonas" :key="zona" :zona="zona" />
              </div>
            </q-item-section>
            <q-item-section side>
              <q-icon v-if="selectedTituloID === t.id" name="check_circle" color="blue-7" />
            </q-item-section>
          </q-item>
        </q-list>

        <q-banner v-if="submitError" rounded dense class="bg-red-1 text-red-8 q-mb-md">
          {{ submitError }}
        </q-banner>
        <q-btn unelevated class="et-btn-primary full-width" label="INICIAR VIAGEM"
               :loading="submitLoading" :disable="!selectedCarreiraID || !selectedTituloID"
               style="height: 50px" @click="confirmarCheckIn" />
      </template>

      <!-- PASSO 3: sucesso -->
      <template v-if="step === 3">
        <div class="success-wrap">
          <q-icon name="check_circle" size="72px" color="green-6" />
          <p class="success-title">Viagem iniciada!</p>
          <p class="success-sub">
            {{ viagem?.carreiraNome }}<br />
            desde <strong>{{ paragem?.nome }}</strong>
          </p>
          <q-btn unelevated class="et-btn-primary q-mt-xl" label="Voltar ao início"
                 style="min-width: 200px; height: 50px" @click="$router.push('/app')" />
        </div>
      </template>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import PageHeader from 'components/PageHeader.vue';
import QrScanner from 'components/QRScanner.vue';
import ZoneBadge from 'components/ZoneBadge.vue';
import type { ParagemDTO, CarreiraDTO, TituloDTO, ViagemDTO } from 'components/models';
import { tipoIcon } from 'src/utils/titulo';
import { titlesService } from 'src/services/titlesService';
import { tripsService } from 'src/services/tripsService';
import { stopsService } from 'src/services/stopsService';

const step = ref<1 | 2 | 3>(1);

const qrLoading = ref(false);
const qrError   = ref('');
const paragem   = ref<ParagemDTO | null>(null);

const carreiras          = ref<CarreiraDTO[]>([]);
const carreirasLoading   = ref(false);
const titulos            = ref<TituloDTO[]>([]);
const titulosLoading     = ref(false);
const selectedCarreiraID = ref<number | null>(null);
const selectedTituloID   = ref<number | null>(null);

const titulosDisponiveis = computed(() =>
  titulos.value.filter((t) => t.valido && (t.saldoRestante == null || t.saldoRestante > 0)),
);

const submitLoading = ref(false);
const submitError   = ref('');
const viagem        = ref<ViagemDTO | null>(null);

onMounted(() => { void loadTitulos(); });

async function loadTitulos() {
  titulosLoading.value = true;
  try   { titulos.value = await titlesService.getActive(); }
  finally { titulosLoading.value = false; }
}

async function onQrScanned(code: string) {
  if (qrLoading.value || !code || code.length > 100) return;
  qrError.value   = '';
  qrLoading.value = true;
  try {
    paragem.value = await stopsService.getByQr(code);
    await loadCarreiras(paragem.value.id);
    step.value = 2;
  } catch {
    qrError.value = `Paragem não reconhecida: "${code}"`;
  } finally { qrLoading.value = false; }
}

async function loadCarreiras(paragemID: number) {
  carreirasLoading.value = true;
  try {
    carreiras.value = await stopsService.getRoutes(paragemID);
    // pré-selecionar a carreira com horário mais próximo (primeira da lista)
    if (carreiras.value.length > 0) {
      selectedCarreiraID.value = carreiras.value[0]!.id;
    }
  } finally { carreirasLoading.value = false; }
}

async function confirmarCheckIn() {
  submitError.value   = '';
  submitLoading.value = true;
  try {
    viagem.value = await tripsService.checkIn(
      paragem.value!.id,
      selectedCarreiraID.value!,
      selectedTituloID.value!,
    );
    step.value = 3;
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    submitError.value = err?.response?.data?.message ?? 'Ocorreu um erro ao iniciar a viagem. Tente novamente...';
  } finally { submitLoading.value = false; }
}

function reset() {
  step.value               = 1;
  paragem.value            = null;
  carreiras.value          = [];
  selectedCarreiraID.value = null;
  selectedTituloID.value   = null;
  submitError.value        = '';
}
</script>

<style scoped>
.checkin-page { background: var(--et-bg); min-height: 100dvh; }

.step-label { font-size: 11px; font-weight: 700; color: var(--et-subtext); letter-spacing: 0.5px; margin: 0 0 4px; }
.step-title  { font-size: 18px; font-weight: 700; color: var(--et-text); margin: 0 0 16px; }

.info-card {
  background: white; border-radius: 12px; padding: 14px 16px;
  display: flex; align-items: center; gap: 12px; margin-bottom: 4px;
}
.info-card-icon  { width: 40px; height: 40px; background: #eff6ff; border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.info-card-label { font-size: 10px; font-weight: 700; color: var(--et-subtext); letter-spacing: 0.5px; margin: 0; }
.info-card-value { font-size: 15px; font-weight: 600; color: var(--et-text); margin: 2px 0 0; }

.select-list   { background: white; border-radius: 12px; overflow: hidden; border: 1px solid var(--et-border); }
.item-selected { background: #eff6ff !important; }

.titulo-zonas-row { display: flex; flex-wrap: wrap; gap: 4px; margin-top: 4px; }

.empty-warning {
  background: #fffbeb; border-radius: 10px; padding: 12px 16px;
  font-size: 14px; color: #92400e; margin-bottom: 16px;
}

.success-wrap {
  display: flex; flex-direction: column; align-items: center;
  justify-content: center; padding: 60px 24px; text-align: center;
}
.success-title { font-size: 24px; font-weight: 800; color: var(--et-text); margin: 16px 0 8px; }
.success-sub   { font-size: 15px; color: var(--et-subtext); line-height: 1.6; margin: 0; }
</style>