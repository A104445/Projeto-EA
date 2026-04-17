<template>
  <q-dialog :model-value="modelValue" position="bottom" @hide="reset"
            @update:model-value="$emit('update:modelValue', $event)">
    <q-card class="bottom-sheet checkin-sheet">

      <q-card-section class="sheet-header">
        <span class="sheet-title">
          {{ step === 1 ? 'Fazer Check-in' : step === 2 ? 'Confirmar viagem' : 'Viagem iniciada!' }}
        </span>
        <q-btn flat round dense icon="close" @click="$emit('update:modelValue', false)" />
      </q-card-section>

      <!-- passo 1: QR scan -->
      <q-card-section v-if="step === 1" class="sheet-body">
        <p class="sheet-desc">Aponta a câmara ao QR code da paragem de entrada.</p>
        <QrScanner @scan="onQrScanned" />
        <q-banner v-if="qrError" rounded dense class="bg-red-1 text-red-8 q-mt-sm">{{ qrError }}</q-banner>
        <div v-if="qrLoading" class="text-center q-mt-sm">
          <q-spinner color="blue-6" size="28px" />
        </div>
      </q-card-section>

      <!-- passo 2: confirmar carreira + título -->
      <q-card-section v-if="step === 2" class="sheet-body step2-body">
        <div class="info-confirm q-mb-md">
          <q-icon name="location_on" color="blue-7" />
          <span>{{ paragem?.nome }}</span>
          <q-btn flat round dense icon="qr_code_scanner" color="grey-5" size="sm" @click="step = 1" />
        </div>

        <div class="field-label q-mb-xs">CARREIRA</div>
        <div v-if="carreirasLoading" class="text-grey-5 text-caption q-mb-md">A carregar carreiras...</div>
        <div v-else-if="carreiras.length === 0" class="empty-warning q-mb-md">
          Nenhuma carreira passa nesta paragem...
        </div>
        <q-list v-else separator class="select-list q-mb-md">
          <q-item v-for="c in carreiras" :key="c.ID" clickable
                  :class="{ 'item-selected': selectedCarreiraID === c.ID }"
                  @click="selectedCarreiraID = c.ID">
            <q-item-section avatar>
              <q-icon name="directions_bus" :color="selectedCarreiraID === c.ID ? 'blue-7' : 'grey-5'" />
            </q-item-section>
            <q-item-section>
              <q-item-label>{{ c.num }} — {{ c.nome }}</q-item-label>
              <q-item-label v-if="c.proximaChegada" caption>
                <q-icon name="schedule" size="11px" /> próximo: {{ c.proximaChegada }}
              </q-item-label>
            </q-item-section>
            <q-item-section side>
              <q-icon v-if="selectedCarreiraID === c.ID" name="check_circle" color="blue-7" />
            </q-item-section>
          </q-item>
        </q-list>

        <div class="field-label q-mb-xs">TÍTULO A USAR</div>
        <div v-if="titulosLoading" class="text-grey-5 text-caption q-mb-md">A carregar títulos...</div>
        <div v-else-if="titulosDisponiveis.length === 0" class="empty-warning q-mb-md">
          Não tens títulos válidos!
        </div>
        <q-list v-else separator class="select-list q-mb-md">
          <q-item v-for="t in titulosDisponiveis" :key="t.ID" clickable
                  :class="{ 'item-selected': selectedTituloID === t.ID }"
                  @click="selectedTituloID = t.ID">
            <q-item-section avatar>
              <q-icon :name="tipoIcon(t.tipo)" :color="selectedTituloID === t.ID ? 'blue-7' : 'grey-5'" />
            </q-item-section>
            <q-item-section>
              <q-item-label>{{ t.tipo?.replace(/_/g, ' ') }}</q-item-label>
              <div class="titulo-zonas-row">
                <ZoneBadge v-for="zona in t.zonas" :key="zona" :zona="zona" />
              </div>
            </q-item-section>
            <q-item-section side>
              <q-icon v-if="selectedTituloID === t.ID" name="check_circle" color="blue-7" />
            </q-item-section>
          </q-item>
        </q-list>

        <q-banner v-if="submitError" rounded dense class="bg-red-1 text-red-8 q-mb-sm">
          {{ submitError }}
        </q-banner>
      </q-card-section>

      <!-- passo 3: sucess -->
      <q-card-section v-if="step === 3" class="sheet-body text-center q-py-lg step3-body">
        <q-icon name="check_circle" size="56px" color="green-6" />
        <p class="text-h6 q-mt-md q-mb-xs">Check-in realizado com sucesso!</p>
        <p class="text-grey-6 text-caption">
          {{ viagem?.carreiraNum }} — {{ viagem?.carreiraNome }}<br />
          desde <strong>{{ paragem?.nome }}</strong>
        </p>
      </q-card-section>

      <!-- actions -->
      <q-card-actions class="sheet-actions">
        <template v-if="step === 2">
          <q-btn unelevated class="et-btn-primary full-width" label="INICIAR VIAGEM"
                 :loading="submitLoading" :disable="!selectedCarreiraID || !selectedTituloID"
                 style="height: 48px" @click="confirmarCheckIn" />
        </template>
        <template v-if="step === 3">
          <q-btn unelevated class="et-btn-primary full-width" label="OK"
                 style="height: 48px" @click="$emit('update:modelValue', false)" />
        </template>
      </q-card-actions>

    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import QrScanner from 'components/QRScanner.vue';
import ZoneBadge from 'components/ZoneBadge.vue';
import type { ParagemDTO, CarreiraDTO, TituloDTO, ViagemDTO } from 'components/models';
import { tipoIcon } from 'src/utils/titulo';
import { titlesService } from 'src/services/titlesService';
import { tripsService } from 'src/services/tripsService';
import { stopsService } from 'src/services/stopsService';

defineProps<{ modelValue: boolean }>();
const emit  = defineEmits<{
  'update:modelValue': [value: boolean];
  done: [viagem: ViagemDTO];
}>();

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

const submitLoading = ref(false);
const submitError   = ref('');
const viagem        = ref<ViagemDTO | null>(null);

const titulosDisponiveis = computed(() =>
  titulos.value.filter((t) => t.valido && (t.saldoRestante == null || t.saldoRestante > 0)),
);

void loadTitulos();

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
    carreirasLoading.value = true;
    carreiras.value = await stopsService.getRoutes(paragem.value.ID);
    if (carreiras.value.length > 0) selectedCarreiraID.value = carreiras.value[0]!.ID;
    step.value = 2;
  } catch {
    qrError.value = `Paragem não reconhecida: "${code}"`;
  } finally {
    qrLoading.value        = false;
    carreirasLoading.value = false;
  }
}

async function confirmarCheckIn() {
  submitError.value   = '';
  submitLoading.value = true;
  try {
    viagem.value = await tripsService.checkIn(
      paragem.value!.ID,
      selectedCarreiraID.value!,
      selectedTituloID.value!,
    );
    step.value = 3;
    emit('done', viagem.value);
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    submitError.value = err?.response?.data?.message ?? 'Ocorreu um erro ao iniciar a viagem.';
  } finally { submitLoading.value = false; }
}

function reset() {
  step.value               = 1;
  paragem.value            = null;
  carreiras.value          = [];
  selectedCarreiraID.value = null;
  selectedTituloID.value   = null;
  submitError.value        = '';
  qrError.value            = '';
  viagem.value             = null;
}
</script>

<style scoped>
.checkin-sheet { min-height: 60dvh; max-height: 88dvh; display: flex; flex-direction: column; }
.step2-body    { overflow-y: auto; flex: 1; }
.step3-body    { flex: 1; display: flex; flex-direction: column; justify-content: center; align-items: center; }

.info-confirm {
  display: flex; align-items: center; gap: 10px;
  background: #eff6ff; border-radius: 10px; padding: 10px 14px;
  font-size: 14px; font-weight: 600; color: var(--et-text);
}
.info-confirm span { flex: 1; }

.select-list   { background: white; border-radius: 12px; overflow: hidden; border: 1px solid var(--et-border); }
.item-selected { background: #eff6ff !important; }
.titulo-zonas-row { display: flex; flex-wrap: wrap; gap: 4px; margin-top: 4px; }

.empty-warning {
  background: #fffbeb; border-radius: 10px; padding: 12px 16px;
  font-size: 14px; color: #92400e;
}
</style>