<template>
  <q-page class="home-page">

    <div class="home-header">
      <div class="home-greeting-line">
        <span class="home-greeting-label">Olá,&nbsp;</span><span class="home-greeting-name">{{ authStore.firstName }}!</span>
      </div>
      <q-btn flat round icon="notifications_none" color="white" />
    </div>

    <div class="home-content">
      <!-- passe ativo -->
      <div v-if="activePasse" class="pass-card">
        <div class="pass-card-top">
          <span class="pass-label">PASSE ATIVO</span>
          <div class="pass-validade">
            <span class="pass-validade-label">VALIDADE</span>
            <span class="pass-validade-date">{{ formatDate(activePasse.dataFim) }}</span>
          </div>
        </div>
        <div class="pass-card-bottom">
          <p class="pass-type">{{ activePasse.tipo?.replace(/_/g, ' ') }}</p>
          <div class="pass-zones-row">
            <span
              v-for="zona in activePasse.zonas"
              :key="zona"
              class="zone-badge"
              :style="{ background: zonaBadgeColor(zona) }"
            >{{ zonaBadgeLabel(zona) }}</span>
          </div>
        </div>
      </div>
      <div v-else-if="titulosLoading" class="pass-card pass-card-loading">
        <q-skeleton type="rect" height="80px" dark />
      </div>
      <div v-else class="no-pass-card">
        <q-icon name="credit_card_off" size="32px" color="grey-5" />
        <p>Sem passe ativo</p>
      </div>

      <!-- ver bilhetes -->
      <q-btn unelevated class="full-width ver-bilhetes-btn" label="VER BILHETES DISPONÍVEIS"
             @click="$router.push('/app/pagamentos')" />

      <!-- ── viagem em curso (check-out) ── -->
      <div v-if="viagemAtiva" class="viagem-ativa-card">
        <div class="viagem-ativa-header">
          <q-icon name="directions_bus" size="20px" color="white" />
          <span>VIAGEM EM CURSO</span>
          <q-spinner size="16px" color="white" />
        </div>
        <div class="viagem-ativa-body">
          <p class="viagem-ativa-carreira">
            {{ viagemAtiva.carreiraNum }} — {{ viagemAtiva.carreiraNome }}
          </p>
          <p class="viagem-ativa-origem">desde {{ viagemAtiva.origemNome }}</p>
        </div>
        <q-btn unelevated class="checkout-btn full-width" label="FAZER CHECK-OUT"
               @click="checkoutDialog = true" />
      </div>

      <!-- ── check-in (só aparece se não há viagem ativa) ── -->
      <q-item v-else clickable class="checkin-item" @click="$router.push('/app/checkin')">
        <q-item-section avatar>
          <div class="checkin-icon-wrap">
            <q-icon name="qr_code_scanner" size="24px" color="grey-7" />
          </div>
        </q-item-section>
        <q-item-section>
          <q-item-label class="checkin-title">Fazer Check-in</q-item-label>
          <q-item-label caption class="checkin-caption">
            Lê o QR code da paragem para iniciar a viagem.
          </q-item-label>
        </q-item-section>
        <q-item-section side>
          <q-icon name="chevron_right" color="grey-4" />
        </q-item-section>
      </q-item>

      <!-- viagens recentes -->
      <div class="section-header">
        <span class="section-title">Viagens recentes</span>
        <q-btn flat no-caps dense label="Ver Histórico" color="blue-6" size="13px"
               @click="$router.push('/app/percursos')" />
      </div>

      <div v-if="viagensLoading">
        <q-item v-for="i in 2" :key="i">
          <q-item-section avatar><q-skeleton type="QAvatar" size="40px" /></q-item-section>
          <q-item-section>
            <q-skeleton type="text" width="60%" />
            <q-skeleton type="text" width="40%" />
          </q-item-section>
        </q-item>
      </div>
      <div v-else-if="recentViagens.length === 0" class="no-trips">
        <p>Sem viagens recentes</p>
      </div>
      <template v-else>
        <q-item v-for="v in recentViagens" :key="v.ID" clickable class="trip-item"
                @click="$router.push('/app/percursos')">
          <q-item-section avatar>
            <div class="trip-icon-wrap">
              <q-icon :name="tripIcon(v.carreiraNome)" size="20px" color="grey-7" />
            </div>
          </q-item-section>
          <q-item-section>
            <q-item-label class="trip-name">{{ v.carreiraNum }} - {{ v.carreiraNome }}</q-item-label>
            <q-item-label caption>{{ formatDateTime(v.dataHoraEntrada) }}</q-item-label>
          </q-item-section>
          <q-item-section side>
            <q-icon name="chevron_right" color="grey-4" />
          </q-item-section>
        </q-item>
      </template>
    </div>

    <!-- ── check-out (dialog) ── -->
    <q-dialog v-model="checkoutDialog" position="bottom" @hide="resetCheckout">
      <q-card class="bottom-sheet">
        <q-card-section class="sheet-header">
          <span class="sheet-title">Fazer Check-out</span>
          <q-btn flat round dense icon="close" @click="checkoutDialog = false" />
        </q-card-section>

        <q-card-section v-if="!checkoutParagem" class="sheet-body">
          <p class="sheet-desc">Aponta a câmara ao QR da paragem de destino.</p>
          <QrScanner hint="Aponta para o QR code da paragem de saída." @scan="onCheckoutQr" />
          <q-banner v-if="checkoutError" rounded dense class="bg-red-1 text-red-8 q-mt-sm">
            {{ checkoutError }}
          </q-banner>
          <div v-if="checkoutLoading" class="text-center q-mt-sm">
            <q-spinner color="blue-6" size="28px" />
          </div>
        </q-card-section>

        <q-card-section v-else class="sheet-body">
          <p class="sheet-desc">Confirma a paragem de destino:</p>
          <div class="info-confirm">
            <q-icon name="location_on" color="blue-7" />
            <span>{{ checkoutParagem.nome }}</span>
          </div>
          <q-banner v-if="checkoutError" rounded dense class="bg-red-1 text-red-8 q-mt-sm q-mb-sm">
            {{ checkoutError }}
          </q-banner>
        </q-card-section>

        <q-card-actions v-if="checkoutParagem" class="sheet-actions">
          <q-btn flat label="Trocar paragem" color="grey-6" @click="checkoutParagem = null" />
          <q-btn unelevated class="et-btn-primary" label="CONFIRMAR SAÍDA"
                 :loading="checkoutLoading" @click="confirmarCheckout" />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- check-out (dialog result) -->
    <q-dialog v-model="resultDialog" position="bottom">
      <q-card class="bottom-sheet">
        <q-card-section class="text-center q-pt-lg q-pb-sm">
          <q-icon :name="resultIrregular ? 'warning' : 'check_circle'"
                  size="56px" :color="resultIrregular ? 'orange-7' : 'green-6'" />
          <p class="text-h6 q-mt-md q-mb-xs">
            {{ resultIrregular ? 'Viagem irregular!' : 'Viagem concluída!' }}
          </p>
          <p class="text-grey-6 text-caption">
            {{ resultIrregular
              ? 'O título não cobre todas as zonas percorridas.'
              : 'Check-out realizado com sucesso!' }}
          </p>
        </q-card-section>
        <q-card-actions class="sheet-actions">
          <q-btn unelevated class="et-btn-primary full-width" label="OK" style="height:48px"
                 @click="resultDialog = false" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useAuthStore } from 'stores/auth';
import { api } from 'boot/axios';
import QrScanner from 'components/QRScanner.vue';
import type { TituloDTO, ViagemDTO, ParagemDTO } from 'components/models';

const authStore = useAuthStore();

const titulos        = ref<TituloDTO[]>([]);
const titulosLoading = ref(true);
const viagens        = ref<ViagemDTO[]>([]);
const viagensLoading = ref(true);
const viagemAtiva    = ref<ViagemDTO | null>(null);

const activePasse   = computed(() =>
  titulos.value.find((t) => t.valido && t.tipo?.toLowerCase().includes('passe')));
const recentViagens = computed(() =>
  viagens.value.filter((v) => v.estado !== 'EM_CURSO').slice(0, 3));

// check-out
const checkoutDialog  = ref(false);
const checkoutParagem = ref<ParagemDTO | null>(null);
const checkoutLoading = ref(false);
const checkoutError    = ref('');
const resultDialog    = ref(false);
const resultIrregular = ref(false);

onMounted(() => {
  void Promise.allSettled([loadTitulos(), loadViagens(), loadViagemAtiva()]);
});

async function loadTitulos() {
  try {
    const { data } = await api.get<TituloDTO[]>('/api/tickets/mine/active');
    titulos.value = data;
  } finally { titulosLoading.value = false; }
}

async function loadViagens() {
  try {
    const { data } = await api.get<ViagemDTO[]>('/api/trips/history');
    viagens.value = data;
  } finally { viagensLoading.value = false; }
}

async function loadViagemAtiva() {
  try {
    const { data } = await api.get<ViagemDTO>('/api/trips/active');
    viagemAtiva.value = data ?? null;
  } catch { viagemAtiva.value = null; }
}

async function onCheckoutQr(code: string) {
  if (checkoutLoading.value) return;
  if (!code || code.length > 100) return;
  checkoutError.value    = '';
  checkoutLoading.value = true;
  try {
    const { data } = await api.get<ParagemDTO>('/api/stops/by-qr', { params: { qr: code } });
    checkoutParagem.value = data;
  } catch {
    checkoutError.value = `Paragem não reconhecida: "${code}!"`;
  } finally { checkoutLoading.value = false; }
}

async function confirmarCheckout() {
  if (!viagemAtiva.value || !checkoutParagem.value) return;
  checkoutError.value    = '';
  checkoutLoading.value = true;
  try {
    const { data } = await api.post<ViagemDTO>(
      `/api/trips/${viagemAtiva.value.ID}/check-out`,
      { paragemID: checkoutParagem.value.ID },
    );
    resultIrregular.value = data.irregular ?? false;
    viagemAtiva.value     = null;
    checkoutDialog.value  = false;
    resultDialog.value    = true;
    void loadViagens();
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    checkoutError.value = err?.response?.data?.message ?? 'Ocorreu um erro ao fazer check-out.';
  } finally { checkoutLoading.value = false; }
}

function resetCheckout() {
  checkoutParagem.value = null;
  checkoutError.value    = '';
}

function formatDate(dateStr?: string): string {
  if (!dateStr) return '';
  return new Date(dateStr)
    .toLocaleDateString('pt-PT', { day: '2-digit', month: 'short', year: 'numeric' })
    .toUpperCase();
}

function formatDateTime(dateStr?: string): string {
  if (!dateStr) return '';
  const d = new Date(dateStr);
  const today     = new Date();
  const yesterday = new Date(today); yesterday.setDate(today.getDate() - 1);
  const time = d.toLocaleTimeString('pt-PT', { hour: '2-digit', minute: '2-digit' });
  if (d.toDateString() === today.toDateString())     return `Hoje, ${time}`;
  if (d.toDateString() === yesterday.toDateString()) return `Ontem, ${time}`;
  return d.toLocaleDateString('pt-PT', { day: '2-digit', month: 'long' }) + ', ' + time;
}

const ZONE_COLORS: Record<string, string> = {
  A: '#3b82f6',
  B: '#22c55e',
  C: '#f97316',
  D: '#a855f7',
  E: '#ec4899',
};

function zonaBadgeLabel(zona: string): string {
  const match = zona.match(/Zona\s+([A-Z])/i);
  if (match?.[1]) return `Zona ${match[1].toUpperCase()}`;
  return zona;
}

function zonaBadgeColor(zona: string): string {
  const match = zona.match(/Zona\s+([A-Z])/i);
  return match?.[1] ? (ZONE_COLORS[match[1].toUpperCase()] ?? '#6b7280') : '#6b7280';
}

function tripIcon(nome: string): string {
  const n = (nome ?? '').toLowerCase();
  if (n.includes('metro'))  return 'subway';
  if (n.includes('comboio') || n.includes('cp')) return 'train';
  if (n.includes('ferry') || n.includes('barco')) return 'directions_boat';
  return 'directions_bus';
}
</script>

<style scoped>
.home-page {
  background: var(--et-bg);
  min-height: 100dvh;
}

.home-header {
  background: var(--et-blue); padding: 52px 24px 28px;
  display: flex; justify-content: space-between; align-items: flex-start;
}
.home-greeting-line  { display: flex; align-items: baseline; }
.home-greeting-label { color: rgba(255,255,255,0.8); font-size: 22px; }
.home-greeting-name  { color: white; font-size: 30px; font-weight: 800; }

.home-content { padding: 20px 16px; display: flex; flex-direction: column; gap: 12px; }

.pass-card { background: var(--et-navy); border-radius: 16px; padding: 16px 20px; color: white; }
.pass-card-loading { min-height: 100px; }
.pass-card-top { display: flex; justify-content: space-between; margin-bottom: 12px; }
.pass-label { font-size: 11px; font-weight: 700; color: rgba(255,255,255,0.6); letter-spacing: 0.5px; }
.pass-validade { text-align: right; }
.pass-validade-label {
  display: block;
  font-size: 10px;
  color: rgba(255,255,255,0.6);
  letter-spacing: 0.5px;
}
.pass-validade-date  {
  font-size: 12px;
  font-weight: 600;
  color: rgba(255,255,255,0.9);
}
.pass-type      { font-size: 20px; font-weight: 700; margin: 0; }
.pass-zones-row { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 8px; }
.zone-badge {
  font-size: 11px; font-weight: 700; color: white;
  padding: 3px 10px; border-radius: 20px;
  letter-spacing: 0.3px;
}

.no-pass-card {
  background: white; border-radius: 16px; padding: 24px; text-align: center;
  color: #9ca3af; font-size: 14px; border: 1px dashed #e5e7eb;
}
.no-pass-card p { margin: 8px 0 0; }

.ver-bilhetes-btn {
  background: #1f2937 !important; color: white !important;
  border-radius: 10px !important; height: 48px;
  font-size: 13px !important; font-weight: 700 !important; letter-spacing: 0.5px;
}

/* viagem ativa */
.viagem-ativa-card {
  background: #16a34a; border-radius: 16px; padding: 16px; color: white;
}
.viagem-ativa-header {
  display: flex; align-items: center; gap: 8px;
  font-size: 11px; font-weight: 700; letter-spacing: 0.5px;
  color: rgba(255,255,255,0.8); margin-bottom: 10px;
}
.viagem-ativa-carreira { font-size: 17px; font-weight: 700; margin: 0; }
.viagem-ativa-origem   { font-size: 13px; color: rgba(255,255,255,0.75); margin: 4px 0 14px; }
.checkout-btn {
  background: rgba(255,255,255,0.2) !important; color: white !important;
  border-radius: 10px !important; height: 44px; font-weight: 700 !important; font-size: 13px !important;
}

/* check-in item */
.checkin-item { background: white; border-radius: 12px; padding: 12px 16px; }
.checkin-icon-wrap {
  width: 44px; height: 44px; background: #f3f4f6; border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
}
.checkin-title   { font-weight: 600; font-size: 15px; color: var(--et-text); }
.checkin-caption { font-size: 12px; color: var(--et-subtext); white-space: normal; }

.section-header { display: flex; justify-content: space-between; align-items: center; padding: 4px 4px 0; }
.section-title  { font-weight: 700; font-size: 16px; color: var(--et-text); }

.trip-item { background: white; border-radius: 12px; padding: 12px 16px; }
.trip-icon-wrap {
  width: 40px; height: 40px; background: #f3f4f6; border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
}
.trip-name { font-weight: 600; font-size: 14px; color: var(--et-text); }
.no-trips { text-align: center; padding: 24px; color: var(--et-subtext); font-size: 14px; background: white; border-radius: 12px; }

/* check-out btm */
.bottom-sheet { border-radius: 20px 20px 0 0 !important; width: 100%; max-width: 430px; margin: 0 auto; }
.sheet-header { display: flex; justify-content: space-between; align-items: center; padding-bottom: 0; }
.sheet-title  { font-size: 18px; font-weight: 700; color: var(--et-text); }
.sheet-body   { padding-top: 12px; }
.sheet-desc   { font-size: 14px; color: var(--et-subtext); margin: 0 0 16px; }
.sheet-actions { padding: 8px 16px 24px; display: flex; gap: 8px; justify-content: flex-end; }

.info-confirm {
  display: flex; align-items: center; gap: 10px;
  background: #eff6ff; border-radius: 10px; padding: 12px 16px;
  font-size: 15px; font-weight: 600; color: var(--et-text);
}
</style>
