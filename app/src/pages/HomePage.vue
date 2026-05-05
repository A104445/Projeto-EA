<template>
  <q-page class="home-page">

    <!-- header azul: greeting + passe + botão de bilhetes -->
    <div class="home-header">
      <div class="home-header-top">
        <div class="home-greeting">Olá, {{ authStore.firstName }}!</div>
        <q-btn flat round icon="notifications_none" color="white" @click="$router.push('/app/notificacoes')">
          <q-badge v-if="numNotificacoes > 0" color="red" floating :label="numNotificacoes" />
        </q-btn>
      </div>

      <!-- passe ativo -->
      <div v-if="activePasse" class="pass-card">
        <div class="pass-card-top">
          <div>
            <span class="pass-label">passe ATIVO</span>
            <p class="pass-type">{{ activePasse.tipo?.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase()) }}</p>
            <div class="pass-zones-row">
              <ZoneBadge v-for="zona in activePasse.zonas" :key="zona" :zona="zona" />
            </div>
          </div>
          <div class="pass-validade">
            <span class="pass-validade-label">validade:</span>
            <span class="pass-validade-date">{{ formatDate(activePasse.dataFim) }}</span>
          </div>
        </div>
      </div>
      <div v-else-if="titulosLoading" class="pass-card pass-card-loading">
        <q-skeleton type="rect" height="80px" dark />
      </div>
      <div v-else class="no-pass-card">
        <q-icon name="credit_card_off" size="32px" color="grey-5" />
        <p>Sem passe ativo.</p>
      </div>

    </div>

    <div class="home-content">
      <!-- ver bilhetes -->
      <q-btn unelevated class="full-width ver-bilhetes-btn" label="VER BILHETES DISPONÍVEIS"
             @click="$router.push('/app/bilhetes/meus')" />

      <!-- ── viagem em curso (check-out) ── -->
      <div v-if="viagemAtiva" class="viagem-ativa-card">
        <div class="viagem-ativa-header">
          <q-icon name="directions_bus" size="20px" color="white" />
          <span>VIAGEM EM CURSO</span>
          <q-spinner size="16px" color="white" />
        </div>
        <div class="viagem-ativa-body">
          <p class="viagem-ativa-carreira">{{ viagemAtiva.carreiraNum }} — {{ viagemAtiva.carreiraNome }}</p>
          <p class="viagem-ativa-origem">desde {{ viagemAtiva.origemNome }}</p>
        </div>
        <q-btn unelevated class="checkout-btn full-width" label="FAZER CHECK-OUT"
               @click="checkoutDialog = true" />
      </div>

      <!-- ── check-in (só sem viagem ativa) ── -->
      <q-item v-if="!viagemAtiva" clickable class="action-item" @click="checkinDialog = true">
        <q-item-section avatar>
          <div class="action-icon-wrap bg-blue-1">
            <q-icon name="qr_code_scanner" size="22px" color="blue-7" />
          </div>
        </q-item-section>
        <q-item-section>
          <q-item-label class="action-title">Fazer Check-in</q-item-label>
          <q-item-label caption class="action-caption">Lê o QR code da paragem para iniciar a viagem.</q-item-label>
        </q-item-section>
        <q-item-section side><q-icon name="chevron_right" color="grey-4" /></q-item-section>
      </q-item>

      <!-- viagens recentes -->
      <div class="section-header">
        <span class="section-title">Viagens recentes</span>
        <q-btn flat no-caps dense label="Ver Histórico" class="ver-history-btn"
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
        <q-item v-for="v in recentViagens" :key="v.id" clickable class="trip-item"
                @click="$router.push('/app/percursos')">
          <q-item-section avatar>
            <div class="trip-icon-wrap">
              <q-icon :name="tripIconFromNome(v.carreiraNome)" size="20px" color="grey-7" />
            </div>
          </q-item-section>
          <q-item-section>
            <q-item-label class="trip-name">{{ v.carreiraNum }} - {{ v.carreiraNome }}</q-item-label>
            <q-item-label caption>{{ formatDateTime(v.dataHoraEntrada) }}</q-item-label>
          </q-item-section>
          <q-item-section side><q-icon name="chevron_right" color="grey-4" /></q-item-section>
        </q-item>
      </template>
    </div>

    <!-- ── check-in (sheet) ── -->
    <CheckInSheet v-model="checkinDialog" @done="onCheckinDone" />

    <!-- ── check-out (sheet) ── -->
    <q-dialog v-model="checkoutDialog" position="bottom" @hide="resetCheckout">
      <q-card class="bottom-sheet">
        <q-card-section class="sheet-header">
          <span class="sheet-title">Fazer Check-out</span>
          <q-btn flat round dense icon="close" @click="checkoutDialog = false" />
        </q-card-section>

        <q-card-section v-if="!checkoutParagem" class="sheet-body">
          <p class="sheet-desc">Aponta a câmara ao QR da paragem de destino.</p>
          <QrScanner @scan="onCheckoutQr" />
          <q-banner v-if="checkoutError" rounded dense class="bg-red-1 text-red-8 q-mt-sm">{{ checkoutError }}</q-banner>
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
          <q-banner v-if="checkoutError" rounded dense class="bg-red-1 text-red-8 q-mt-sm q-mb-sm">{{ checkoutError }}</q-banner>
        </q-card-section>

        <q-card-actions v-if="checkoutParagem" class="sheet-actions">
          <q-btn flat label="Trocar paragem" color="grey-6" @click="checkoutParagem = null" />
          <q-btn unelevated class="et-btn-primary" label="CONFIRMAR SAÍDA"
                 :loading="checkoutLoading" @click="confirmarCheckout" />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- resultados -->
    <ResultDialog v-model="resultDialog"
      :icon="resultIrregular ? 'warning' : 'check_circle'"
      :icon-color="resultIrregular ? 'orange-7' : 'green-6'"
      :title="resultIrregular ? 'Viagem irregular!' : 'Viagem concluída!'"
      :message="resultIrregular ? 'O título não cobre todas as zonas percorridas!' : 'Check-out realizado com sucesso!'" />
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useAuthStore } from 'stores/auth';
import QrScanner from 'components/QRScanner.vue';
import ZoneBadge from 'components/ZoneBadge.vue';
import ResultDialog from 'components/ResultDialog.vue';
import CheckInSheet from 'components/CheckInSheet.vue';
import type { TituloDTO, ViagemDTO, ParagemDTO } from 'components/models';
import { formatDate, formatDateTime } from 'src/utils/formatters';
import { tripIconFromNome } from 'src/utils/titulo';
import { titlesService } from 'src/services/titlesService';
import { tripsService } from 'src/services/tripsService';
import { stopsService } from 'src/services/stopsService';
import { notificacoesService } from 'src/services/notificacoesService';
import { useNotificacoesStore } from 'stores/notificacoes';

const authStore = useAuthStore();

const titulos        = ref<TituloDTO[]>([]);
const titulosLoading = ref(true);
const viagens        = ref<ViagemDTO[]>([]);
const viagensLoading = ref(true);
const viagemAtiva    = ref<ViagemDTO | null>(null);
const notificacoesStore = useNotificacoesStore();
const numNotificacoes = computed(() => notificacoesStore.count);

const activePasse   = computed(() =>
  titulos.value.find((t) => t.valido && t.tipo?.toLowerCase().includes('passe')));
const recentViagens = computed(() =>
  viagens.value.filter((v) => v.estado !== 'EM_CURSO').slice(0, 3));

// check-in
const checkinDialog = ref(false);

// check-out
const checkoutDialog  = ref(false);
const checkoutParagem = ref<ParagemDTO | null>(null);
const checkoutLoading = ref(false);
const checkoutError   = ref('');

// resultado check-out
const resultDialog    = ref(false);
const resultIrregular = ref(false);


async function loadTitulos() {
  try   { titulos.value = await titlesService.getActive(); }
  finally { titulosLoading.value = false; }
}
async function loadViagens() {
  try   { viagens.value = await tripsService.getHistory(); }
  finally { viagensLoading.value = false; }
}
async function loadViagemAtiva() {
  try   { viagemAtiva.value = await tripsService.getActive(); }
  catch { viagemAtiva.value = null; }
}
async function loadNotificacoes() {
  try { notificacoesStore.set((await notificacoesService.getAll()).length); }
  catch { notificacoesStore.set(0); }
}

onMounted(() => {
  void Promise.allSettled([loadTitulos(), loadViagens(), loadViagemAtiva(), loadNotificacoes()]);
});

function onCheckinDone(viagem: ViagemDTO) {
  viagemAtiva.value = viagem;
  void loadViagens();
}

async function onCheckoutQr(code: string) {
  if (checkoutLoading.value || !code || code.length > 100) return;
  checkoutError.value   = '';
  checkoutLoading.value = true;
  try {
    checkoutParagem.value = await stopsService.getByQr(code);
  } catch {
    checkoutError.value = `Paragem não reconhecida: "${code}"`;
  } finally { checkoutLoading.value = false; }
}

async function confirmarCheckout() {
  if (!viagemAtiva.value || !checkoutParagem.value) return;
  checkoutError.value   = '';
  checkoutLoading.value = true;
  try {
    const result = await tripsService.checkOut(viagemAtiva.value.id, checkoutParagem.value.id);
    resultIrregular.value = result.irregular ?? false;
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
  checkoutError.value   = '';
}

</script>

<style scoped>
.home-page { background: #F6F6F6; min-height: 100dvh; }

.home-header {
  background: #719BF5;
  box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.25);
  border-radius: 0 0 5px 5px;
  padding: 52px 32px 56px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.home-header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.home-greeting {
  font-family: 'Roboto', sans-serif;
  font-weight: 700;
  font-size: 40px;
  line-height: 48px;
  color: #FFFFFF;
  text-shadow: 0px 2px 20px rgba(0, 0, 0, 0.25);
}

.pass-card {
  background: rgba(54, 76, 123, 0.56);
  border-radius: 20px;
  padding: 16px 20px;
  color: white;
}
.pass-card-loading { min-height: 100px; }
.pass-card-top { display: flex; justify-content: space-between; align-items: flex-start; }
.pass-label {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.5px;
  color: rgba(255,255,255,0.6);
}
.pass-type {
  font-family: 'Roboto', sans-serif;
  font-size: 25px;
  font-weight: 700;
  color: #FFFFFF;
  text-shadow: 0px 2px 20px rgba(0, 0, 0, 0.25);
  margin: 4px 0 0;
}
.pass-zones-row { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 8px; }
.pass-validade { text-align: right; align-self: flex-start; }
.pass-validade-label {
  display: block;
  font-size: 10px;
  font-weight: 700;
  color: rgba(255,255,255,0.6);
  letter-spacing: 0.5px;
}
.pass-validade-date {
  font-size: 12px;
  font-weight: 600;
  color: rgba(255,255,255,0.9);
}

.no-pass-card {
  background: rgba(54, 76, 123, 0.3); border-radius: 20px; padding: 24px; text-align: center;
  color: rgba(255,255,255,0.7); font-size: 14px;
}
.no-pass-card p { margin: 8px 0 0; }

.ver-bilhetes-btn {
  background: #364C7B !important;
  color: white !important;
  border-radius: 20px !important;
  height: 57px;
  font-size: 13px !important;
  font-weight: 700 !important;
  letter-spacing: 0.5px;
  position: relative;
  z-index: 1;
}

.home-content {
  padding: 0 32px 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: -28px; /* metade da altura do botão → sobrepõe a borda */
}

.viagem-ativa-card { background: #16a34a; border-radius: 16px; padding: 16px; color: white; }
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

/* ── check-in ── */
.action-item { background: white; border-radius: 8px; padding: 12px 16px; }
.action-icon-wrap {
  width: 40px; height: 40px;
  background: rgba(113, 155, 245, 0.13);
  box-shadow: 0px 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
}
.action-title {
  font-family: 'Roboto', sans-serif;
  font-weight: 800;
  font-size: 18px;
  color: #3C3A3A;
}
.action-caption {
  font-family: 'Jaldi', sans-serif;
  font-size: 13px;
  font-weight: 400;
  color: rgba(60, 58, 58, 0.82);
  white-space: normal;
}

/* ── viagens recentes ── */
.section-header { display: flex; justify-content: space-between; align-items: center; padding: 4px 4px 0; }
.section-title {
  font-family: 'Roboto', sans-serif;
  font-weight: 800;
  font-size: 18px;
  color: #3C3A3A;
}

.trip-item { background: white; border-radius: 8px; padding: 12px 16px; }
.trip-icon-wrap {
  width: 40px; height: 40px;
  background: rgba(113, 155, 245, 0.13);
  box-shadow: 0px 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
}
.trip-name {
  font-family: 'Roboto', sans-serif;
  font-weight: 700;
  font-size: 16px;
  color: #3C3A3A;
}
.trip-name + :deep(.q-item__label--caption) {
  font-family: 'Jaldi', sans-serif;
  font-size: 13px;
  color: rgba(60, 58, 58, 0.82);
}
.no-trips { text-align: center; padding: 24px; color: var(--et-subtext); font-size: 14px; background: white; border-radius: 8px; }

.info-confirm {
  display: flex; align-items: center; gap: 10px;
  background: #eff6ff; border-radius: 10px; padding: 12px 16px;
  font-size: 15px; font-weight: 600; color: var(--et-text);
}

/* ── grid de ações ── */
.quick-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.quick-btn {
  background: white; border-radius: 14px; border: none; cursor: pointer;
  padding: 18px 10px 14px; display: flex; flex-direction: column;
  align-items: center; gap: 10px;
  font-size: 12px; font-weight: 700; color: var(--et-text);
  text-align: center; line-height: 1.3;
  transition: background 0.15s;
}
.quick-btn:active { background: #f3f4f6; }
.quick-btn-icon {
  width: 52px; height: 52px; border-radius: 14px;
  display: flex; align-items: center; justify-content: center;
}

/* ── horários (sheet) ── */
.horarios-sheet { max-height: 88dvh; display: flex; flex-direction: column; }
.horarios-header-left { display: flex; align-items: center; gap: 4px; flex: 1; min-width: 0; }
.horarios-header-left .sheet-title { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.select-list    { background: white; border-radius: 12px; overflow: hidden; border: 1px solid var(--et-border); }

.horarios-list { display: flex; flex-direction: column; gap: 8px; }
.horario-row   { background: white; border-radius: 10px; padding: 12px 14px; }
.horario-paragem { font-size: 13px; font-weight: 600; color: var(--et-text); margin-bottom: 6px; }
.horario-times   { display: flex; flex-wrap: wrap; gap: 6px; }
.horario-chip {
  font-size: 12px; font-weight: 600; padding: 3px 10px;
  border-radius: 20px; background: #eff6ff; color: var(--et-blue);
}
.horario-chip--past { background: #f3f4f6; color: #9ca3af; }

.ver-history-btn {
  font-family: 'Jaldi', sans-serif !important;
  font-size: 13px !important;
  font-weight: 400 !important;
  color: #364C7B !important;
}
</style>