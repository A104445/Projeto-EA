<template>
  <q-page class="pagamentos-page">

    <div class="page-header">
      <h1 class="page-title">Pagamentos</h1>
    </div>

    <div class="page-content">
      <div class="section-label">Os meus títulos ativos</div>

      <div v-if="titulosLoading" class="q-mb-md">
        <q-item v-for="i in 2" :key="i" class="titulo-card">
          <q-item-section>
            <q-skeleton type="text" width="50%" />
            <q-skeleton type="text" width="70%" />
          </q-item-section>
        </q-item>
      </div>

      <div v-else-if="titulos.length === 0" class="empty-state">
        <q-icon name="credit_card_off" size="32px" color="grey-4" />
        <p>Nenhum título ativo...</p>
      </div>

      <template v-else>
        <div v-for="t in titulos" :key="t.ID" class="titulo-card">
          <div class="titulo-card-left">
            <q-icon :name="tipoIcon(t.tipo)" size="22px" :color="tipoColor(t.tipo)" />
          </div>
          <div class="titulo-card-body">
            <span class="titulo-tipo">{{ t.tipo?.replace(/_/g, ' ') }}</span>
            <div class="titulo-zonas-row">
              <span
                v-for="zona in t.zonas"
                :key="zona"
                class="titulo-zone-badge"
                :style="{ background: zonaColor(zona) }"
              >{{ zonaBadgeLabel(zona) }}</span>
            </div>
            <span v-if="t.dataFim" class="titulo-validade">Válido até {{ formatDate(t.dataFim) }}</span>
            <span v-if="t.saldoRestante != null" class="titulo-validade">
              {{ t.saldoRestante }} viagem(ns) restante(s)
            </span>
          </div>
          <q-badge
            :color="t.valido ? 'green-2' : 'red-2'"
            :text-color="t.valido ? 'green-9' : 'red-9'"
            class="titulo-badge"
          >
            {{ t.valido ? 'Ativo' : 'Inválido' }}
          </q-badge>
        </div>
      </template>

      <!-- adquirir título -->
      <div class="section-label q-mt-lg">Adquirir título</div>

      <div class="compra-grid">
        <!-- passe anual -->
        <div class="compra-card" @click="openDialog('passe')">
          <div class="compra-icon-wrap bg-blue-1">
            <q-icon name="card_membership" size="28px" color="blue-7" />
          </div>
          <div class="compra-info">
            <span class="compra-nome">Passe Anual</span>
            <span class="compra-desc">Viagens ilimitadas por 1 ano.</span>
          </div>
          <q-icon name="chevron_right" color="grey-4" />
        </div>

        <!-- bilhete individual -->
        <div class="compra-card" @click="openDialog('bilhete')">
          <div class="compra-icon-wrap bg-orange-1">
            <q-icon name="confirmation_number" size="28px" color="orange-7" />
          </div>
          <div class="compra-info">
            <span class="compra-nome">Bilhete Individual</span>
            <span class="compra-desc">Uma viagem numa carreira à sua escolha.</span>
          </div>
          <q-icon name="chevron_right" color="grey-4" />
        </div>

        <!-- pacote de viagens -->
        <div class="compra-card" @click="openDialog('pacote')">
          <div class="compra-icon-wrap bg-purple-1">
            <q-icon name="local_activity" size="28px" color="purple-7" />
          </div>
          <div class="compra-info">
            <span class="compra-nome">Pacote de Viagens</span>
            <span class="compra-desc">Escolhe o número de viagens e zonas.</span>
          </div>
          <q-icon name="chevron_right" color="grey-4" />
        </div>
      </div>
    </div>

    <!-- ───── passe anual (dialog) ───── -->
    <q-dialog v-model="dialogs.passe" position="bottom">
      <q-card class="bottom-sheet">
        <q-card-section class="sheet-header">
          <span class="sheet-title">Passe Anual</span>
          <q-btn flat round dense icon="close" @click="dialogs.passe = false" />
        </q-card-section>
        <q-card-section class="sheet-body">
          <p class="sheet-desc">Válido por 12 meses. Seleciona as zonas geográficas que queres cobrir:</p>
          <div class="field-label q-mb-xs">ZONAS</div>
          <div v-if="zonasLoading" class="text-grey-5 text-caption">A carregar zonas...</div>
          <q-banner v-else-if="zonasError" rounded dense class="bg-orange-1 text-orange-9 q-mb-sm">
            {{ zonasError }}
            <template #action>
              <q-btn flat dense label="Tentar novamente" @click="loadZonas" />
            </template>
          </q-banner>
          <div v-else class="zonas-chips">
            <button
              v-for="z in zonas"
              :key="z.ID"
              class="zona-chip"
              :class="{ 'zona-chip-selected': passeForm.zonaIDs.includes(z.ID) }"
              :style="passeForm.zonaIDs.includes(z.ID) ? { background: zonaColor(z.nome), borderColor: zonaColor(z.nome) } : { borderColor: zonaColor(z.nome), color: zonaColor(z.nome) }"
              @click="toggleZona(passeForm.zonaIDs, z.ID)"
            >{{ zonaBadgeLabel(z.nome) }}</button>
          </div>
          <q-banner v-if="errors.passe" rounded dense class="bg-red-1 text-red-8 q-mt-sm">
            {{ errors.passe }}
          </q-banner>
        </q-card-section>
        <q-card-actions class="sheet-actions">
          <q-btn
            unelevated
            class="et-btn-primary full-width"
            label="Confirmar compra"
            :loading="loading.passe"
            :disable="passeForm.zonaIDs.length === 0"
            @click="comprarPasse"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- ───── bilhete individual (dialog) ───── -->
    <q-dialog v-model="dialogs.bilhete" position="bottom">
      <q-card class="bottom-sheet">
        <q-card-section class="sheet-header">
          <span class="sheet-title">Bilhete Individual</span>
          <q-btn flat round dense icon="close" @click="dialogs.bilhete = false" />
        </q-card-section>
        <q-card-section class="sheet-body">
          <p class="sheet-desc">Bilhete válido para uma viagem até ao dia seguinte. Seleciona a carreira:</p>
          <div class="field-label q-mb-xs">CARREIRA</div>
          <div v-if="carreirasLoading" class="text-grey-5 text-caption">A carregar carreiras...</div>
          <q-banner v-else-if="carreirasError" rounded dense class="bg-orange-1 text-orange-9 q-mb-sm">
            {{ carreirasError }}
            <template #action>
              <q-btn flat dense label="Tentar novamente" @click="loadCarreiras" />
            </template>
          </q-banner>
          <template v-else>
            <q-input
              v-model="searchCarreira"
              outlined
              dense
              placeholder="Pesquisar carreira..."
              class="q-mb-sm"
              clearable
            >
              <template #prepend><q-icon name="search" color="grey-5" /></template>
            </q-input>
            <q-list separator class="carreiras-list">
              <q-item
                v-for="c in carreirasFiltradas"
                :key="c.ID"
                clickable
                :class="{ 'carreira-selected': bilheteForm.carreiraID === c.ID }"
                @click="bilheteForm.carreiraID = c.ID"
              >
                <q-item-section avatar>
                  <q-icon name="directions_bus" :color="bilheteForm.carreiraID === c.ID ? 'blue-7' : 'grey-5'" />
                </q-item-section>
                <q-item-section>
                  <q-item-label>{{ c.num }} — {{ c.nome }}</q-item-label>
                  <q-item-label caption>{{ c.paragens?.length ?? 0 }} paragens</q-item-label>
                </q-item-section>
                <q-item-section side>
                  <q-icon v-if="bilheteForm.carreiraID === c.ID" name="check_circle" color="blue-7" />
                </q-item-section>
              </q-item>
              <q-item v-if="carreirasFiltradas.length === 0">
                <q-item-section class="text-grey-5 text-caption text-center">Nenhuma carreira encontrada.</q-item-section>
              </q-item>
            </q-list>
          </template>
          <q-banner v-if="errors.bilhete" rounded dense class="bg-red-1 text-red-8 q-mt-sm">
            {{ errors.bilhete }}
          </q-banner>
        </q-card-section>
        <q-card-actions class="sheet-actions">
          <q-btn
            unelevated
            class="et-btn-primary full-width"
            label="Confirmar compra"
            :loading="loading.bilhete"
            :disable="!bilheteForm.carreiraID"
            @click="comprarBilhete"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- ───── pacote de viagens (dialog) ───── -->
    <q-dialog v-model="dialogs.pacote" position="bottom">
      <q-card class="bottom-sheet">
        <q-card-section class="sheet-header">
          <span class="sheet-title">Pacote de Viagens</span>
          <q-btn flat round dense icon="close" @click="dialogs.pacote = false" />
        </q-card-section>
        <q-card-section class="sheet-body">
          <p class="sheet-desc">Escolhe o número de viagens e as zonas a cobrir: </p>

          <div class="field-label q-mb-xs">Nº DE VIAGENS</div>
          <div class="num-viagens-row">
            <q-btn flat round icon="remove" :disable="pacoteForm.numViagens <= 1" @click="pacoteForm.numViagens--" />
            <span class="num-viagens-val">{{ pacoteForm.numViagens }}</span>
            <q-btn flat round icon="add" @click="pacoteForm.numViagens++" />
          </div>

          <div class="field-label q-mt-md q-mb-xs">ZONAS</div>
          <div v-if="zonasLoading" class="text-grey-5 text-caption">A carregar zonas...</div>
          <q-banner v-else-if="zonasError" rounded dense class="bg-orange-1 text-orange-9 q-mb-sm">
            {{ zonasError }}
            <template #action>
              <q-btn flat dense label="Tentar novamente" @click="loadZonas" />
            </template>
          </q-banner>
          <div v-else class="zonas-chips">
            <button
              v-for="z in zonas"
              :key="z.ID"
              class="zona-chip"
              :class="{ 'zona-chip-selected': pacoteForm.zonaIDs.includes(z.ID) }"
              :style="pacoteForm.zonaIDs.includes(z.ID) ? { background: zonaColor(z.nome), borderColor: zonaColor(z.nome) } : { borderColor: zonaColor(z.nome), color: zonaColor(z.nome) }"
              @click="toggleZona(pacoteForm.zonaIDs, z.ID)"
            >{{ zonaBadgeLabel(z.nome) }}</button>
          </div>
          <q-banner v-if="errors.pacote" rounded dense class="bg-red-1 text-red-8 q-mt-sm">
            {{ errors.pacote }}
          </q-banner>
        </q-card-section>
        <q-card-actions class="sheet-actions">
          <q-btn
            unelevated
            class="et-btn-primary full-width"
            label="Confirmar compra"
            :loading="loading.pacote"
            :disable="pacoteForm.zonaIDs.length === 0"
            @click="comprarPacote"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- success dialog -->
    <q-dialog v-model="successDialog" position="bottom">
      <q-card class="bottom-sheet">
        <q-card-section class="text-center q-pt-lg q-pb-sm">
          <q-icon name="check_circle" size="56px" color="green-6" />
          <p class="text-h6 q-mt-md q-mb-xs">Compra concluída!</p>
          <p class="text-grey-6 text-caption">O teu título foi adicionado à conta.</p>
        </q-card-section>
        <q-card-actions class="sheet-actions">
          <q-btn unelevated class="et-btn-primary full-width" label="OK" style="height:48px" @click="successDialog = false" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { api } from 'boot/axios';
import type { TituloDTO, ZonaDTO, CarreiraDTO } from 'components/models';

const titulos = ref<TituloDTO[]>([]);
const titulosLoading = ref(true);

const zonas = ref<ZonaDTO[]>([]);
const zonasLoading = ref(true);
const zonasError = ref('');

const carreiras = ref<CarreiraDTO[]>([]);
const carreirasLoading = ref(true);
const carreirasError = ref('');

const dialogs = reactive({ passe: false, bilhete: false, pacote: false });
const loading = reactive({ passe: false, bilhete: false, pacote: false });
const errors = reactive({ passe: '', bilhete: '', pacote: '' });
const successDialog = ref(false);

const passeForm = reactive<{ zonaIDs: number[] }>({ zonaIDs: [] });
const bilheteForm = reactive<{ carreiraID: number | null }>({ carreiraID: null });
const searchCarreira = ref('');
const carreirasFiltradas = computed(() => {
  const q = searchCarreira.value.trim().toLowerCase();
  if (!q) return carreiras.value;
  return carreiras.value.filter(
    (c) => c.num.toLowerCase().includes(q) || c.nome.toLowerCase().includes(q),
  );
});
const pacoteForm = reactive<{ numViagens: number; zonaIDs: number[] }>({ numViagens: 5, zonaIDs: [] });

onMounted(() => {
  void loadTitulos();
  void loadZonas();
  void loadCarreiras();
});

async function loadTitulos() {
  try {
    const { data } = await api.get<TituloDTO[]>('/api/tickets/mine');
    titulos.value = data.filter((t) => t.valido);
  } finally {
    titulosLoading.value = false;
  }
}

async function loadZonas() {
  zonasError.value = '';
  try {
    const { data } = await api.get<ZonaDTO[]>('/api/zones');
    zonas.value = data;
  } catch (e: unknown) {
    const err = e as { response?: { status?: number }; message?: string };
    zonasError.value = `Ocorreu um erro ao carregar as zonas (${err?.response?.status ?? err?.message ?? 'sem resposta'})`;
  } finally {
    zonasLoading.value = false;
  }
}

async function loadCarreiras() {
  carreirasError.value = '';
  try {
    const { data } = await api.get<CarreiraDTO[]>('/api/routes');
    carreiras.value = data;
  } catch (e: unknown) {
    const err = e as { response?: { status?: number }; message?: string };
    carreirasError.value = `Ocorreu um erro ao carregar carreiras (${err?.response?.status ?? err?.message ?? 'sem resposta'})`;
  } finally {
    carreirasLoading.value = false;
  }
}

function openDialog(tipo: 'passe' | 'bilhete' | 'pacote') {
  errors[tipo] = '';
  if (tipo === 'passe') passeForm.zonaIDs = [];
  if (tipo === 'bilhete') bilheteForm.carreiraID = null;
  if (tipo === 'pacote') { pacoteForm.zonaIDs = []; pacoteForm.numViagens = 5; }
  dialogs[tipo] = true;
}

async function comprarPasse() {
  errors.passe = '';
  loading.passe = true;
  try {
    await api.post('/api/tickets/annual-pass', { zonaIDs: passeForm.zonaIDs });
    dialogs.passe = false;
    successDialog.value = true;
    void loadTitulos();
  } catch {
    errors.passe = 'Ocorreu um erro ao adquirir o passe. Tente novamente...';
  } finally {
    loading.passe = false;
  }
}

async function comprarBilhete() {
  errors.bilhete = '';
  loading.bilhete = true;
  try {
    await api.post('/api/tickets/single-ticket', { carreiraID: bilheteForm.carreiraID });
    dialogs.bilhete = false;
    successDialog.value = true;
    void loadTitulos();
  } catch {
    errors.bilhete = 'Ocorreu um erro ao adquirir o bilhete. Tente novamente...';
  } finally {
    loading.bilhete = false;
  }
}

async function comprarPacote() {
  errors.pacote = '';
  loading.pacote = true;
  try {
    await api.post('/api/tickets/trip-pack', {
      numViagens: pacoteForm.numViagens,
      zonaIDs: pacoteForm.zonaIDs,
    });
    dialogs.pacote = false;
    successDialog.value = true;
    void loadTitulos();
  } catch {
    errors.pacote = 'Ocorreu um erro ao adquirir o pacote de bilhetes. Tente novamente...';
  } finally {
    loading.pacote = false;
  }
}

function formatDate(dateStr?: string): string {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleDateString('pt-PT', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  });
}

const ZONE_COLORS: Record<string, string> = {
  A: '#3b82f6', B: '#22c55e', C: '#f97316', D: '#a855f7', E: '#ec4899',
};

function zonaColor(nome: string): string {
  const m = nome.match(/Zona\s+([A-Z])/i);
  return m?.[1] ? (ZONE_COLORS[m[1].toUpperCase()] ?? '#6b7280') : '#6b7280';
}

function zonaBadgeLabel(nome: string): string {
  const m = nome.match(/Zona\s+([A-Z])/i);
  return m?.[1] ? `Zona ${m[1].toUpperCase()}` : nome;
}

function toggleZona(list: number[], id: number) {
  const idx = list.indexOf(id);
  if (idx === -1) list.push(id);
  else list.splice(idx, 1);
}

function tipoIcon(tipo: string): string {
  const t = tipo?.toLowerCase() ?? '';
  if (t.includes('passe')) return 'card_membership';
  if (t.includes('bilhete')) return 'confirmation_number';
  if (t.includes('pacote')) return 'local_activity';
  return 'credit_card';
}

function tipoColor(tipo: string): string {
  const t = tipo?.toLowerCase() ?? '';
  if (t.includes('passe')) return 'blue-7';
  if (t.includes('bilhete')) return 'orange-7';
  if (t.includes('pacote')) return 'purple-7';
  return 'grey-7';
}
</script>

<style scoped>
.pagamentos-page {
  background: var(--et-bg);
  min-height: 100dvh;
}

.page-header {
  background: var(--et-blue);
  padding: 52px 24px 24px;
}

.page-title {
  color: white;
  font-size: 26px;
  font-weight: 800;
  margin: 0;
}

.page-content {
  padding: 20px 16px;
}

.section-label {
  font-size: 13px;
  font-weight: 700;
  color: var(--et-subtext);
  letter-spacing: 0.5px;
  text-transform: uppercase;
  margin-bottom: 10px;
}

/* títulos */
.titulo-card {
  background: white;
  border-radius: 12px;
  padding: 14px 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.titulo-card-left {
  width: 40px;
  height: 40px;
  background: #f3f4f6;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.titulo-card-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.titulo-tipo {
  font-weight: 600;
  font-size: 14px;
  color: var(--et-text);
}

.titulo-zonas-row {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-top: 2px;
}

.titulo-zone-badge {
  font-size: 10px;
  font-weight: 700;
  color: white;
  padding: 2px 8px;
  border-radius: 20px;
}

.titulo-validade {
  font-size: 11px;
  color: #9ca3af;
}

.titulo-badge {
  font-size: 11px;
  padding: 4px 8px;
  border-radius: 20px;
}

.empty-state {
  background: white;
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  color: #9ca3af;
  font-size: 14px;
  border: 1px dashed #e5e7eb;
  margin-bottom: 8px;
}

.empty-state p {
  margin: 8px 0 0;
}

/* Compra cards */
.compra-grid {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.compra-card {
  background: white;
  border-radius: 12px;
  padding: 14px 16px;
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;
  transition: background 0.15s;
}

.compra-card:active {
  background: #f9fafb;
}

.compra-icon-wrap {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.compra-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.compra-nome {
  font-weight: 600;
  font-size: 15px;
  color: var(--et-text);
}

.compra-desc {
  font-size: 12px;
  color: var(--et-subtext);
}

.bottom-sheet {
  border-radius: 20px 20px 0 0 !important;
  width: 100%;
  max-width: 430px;
  margin: 0 auto;
}

.sheet-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 0;
}

.sheet-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--et-text);
}

.sheet-body {
  padding-top: 12px;
}

.sheet-desc {
  font-size: 14px;
  color: var(--et-subtext);
  margin: 0 0 16px;
}

.sheet-actions {
  padding: 8px 16px 24px;
}

.field-label {
  font-size: 11px;
  font-weight: 700;
  color: var(--et-subtext);
  letter-spacing: 0.5px;
}

.zonas-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.zona-chip {
  padding: 8px 18px;
  border-radius: 24px;
  border: 2px solid;
  background: transparent;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.15s, color 0.15s;
  outline: none;
}

.zona-chip-selected {
  color: white !important;
}

.carreiras-list {
  border: 1px solid var(--et-border);
  border-radius: 10px;
  overflow-y: auto;
  max-height: 260px;
}

.carreira-selected {
  background: #eff6ff !important;
}

.num-viagens-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.num-viagens-val {
  font-size: 24px;
  font-weight: 700;
  color: var(--et-text);
  min-width: 32px;
  text-align: center;
}
</style>
