<template>
  <q-page class="perfil-page">
    <div class="page-header">
      <h1 class="page-title">Perfil</h1>
    </div>

    <div class="page-content">
      <!-- avatar + nome -->
      <div class="avatar-section">
        <div class="avatar-circle">
          <span class="avatar-initials">{{ initials }}</span>
        </div>
        <p class="avatar-name">{{ form.nome || authStore.user?.nome }}</p>
        <p class="avatar-email">{{ authStore.user?.email }}</p>
      </div>

      <!-- dados pessoais -->
      <div class="section-card">
        <div class="section-card-header">
          <span class="section-card-title">Dados pessoais</span>
          <q-btn flat dense no-caps
                 :label="editing ? 'Cancelar' : 'Editar'"
                 :color="editing ? 'grey-6' : 'blue-6'"
                 size="13px"
                 @click="toggleEdit" />
        </div>

        <div class="field-row">
          <span class="field-label">NOME</span>
          <input v-if="editing" v-model="form.nome" class="field-input" placeholder="O teu nome" />
          <span v-else class="field-value">{{ authStore.user?.nome || '—' }}</span>
        </div>
        <div class="field-divider" />
        <div class="field-row">
          <span class="field-label">EMAIL</span>
          <span class="field-value">{{ authStore.user?.email }}</span>
        </div>
        <div class="field-divider" />
        <div class="field-row">
          <span class="field-label">TELEMÓVEL</span>
          <input v-if="editing" v-model="form.telefone" class="field-input" placeholder="Ex: 912 345 678" type="tel" />
          <span v-else class="field-value">{{ authStore.user?.telefone || '—' }}</span>
        </div>
        <div class="field-divider" />
        <div class="field-row">
          <span class="field-label">DATA DE NASCIMENTO</span>
          <input v-if="editing" v-model="form.dataNascimento" class="field-input" type="date" />
          <span v-else class="field-value">{{ formatDate(authStore.user?.dataNascimento) }}</span>
        </div>

        <q-banner v-if="saveError" rounded dense class="bg-red-1 text-red-8 q-mt-md">
          {{ saveError }}
        </q-banner>
        <q-btn v-if="editing" unelevated class="et-btn-primary full-width q-mt-md"
               label="GUARDAR ALTERAÇÕES" :loading="saving" style="height: 46px"
               @click="saveProfile" />
      </div>

      <!-- títulos ativos -->
      <div class="section-card">
        <div class="section-card-header">
          <span class="section-card-title">Títulos ativos</span>
          <q-btn flat dense no-caps label="Comprar" color="blue-6" size="13px"
                 @click="$router.push('/app/bilhetes')" />
        </div>

        <div v-if="titulosLoading" class="q-py-sm">
          <q-skeleton v-for="i in 2" :key="i" type="text" class="q-mb-sm" />
        </div>
        <div v-else-if="titulos.length === 0" class="empty-titulos">
          Sem títulos válidos de momento.
        </div>
        <template v-else>
          <div v-for="(t, idx) in titulos" :key="t.ID">
            <div class="titulo-row">
              <div class="titulo-icon-wrap">
                <q-icon :name="tipoIcon(t.tipo)" size="18px" color="blue-7" />
              </div>
              <div class="titulo-info">
                <span class="titulo-tipo">{{ t.tipo?.replace(/_/g, ' ') }}</span>
                <div class="titulo-zones-row">
                  <ZoneBadge v-for="zona in t.zonas" :key="zona" :zona="zona" />
                </div>
              </div>
              <div class="titulo-side">
                <span v-if="t.dataFim" class="titulo-validade">até {{ formatDate(t.dataFim) }}</span>
                <span v-if="t.saldoRestante != null" class="titulo-saldo">{{ t.saldoRestante }} viag.</span>
              </div>
            </div>
            <div v-if="idx < titulos.length - 1" class="field-divider" />
          </div>
        </template>
      </div>

      <!-- logout -->
      <q-btn unelevated class="logout-btn full-width" label="TERMINAR SESSÃO"
             icon="logout" @click="logout" />
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from 'stores/auth';
import ZoneBadge from 'components/ZoneBadge.vue';
import type { TituloDTO } from 'components/models';
import { formatDate } from 'src/utils/formatters';
import { tipoIcon } from 'src/utils/titulo';
import { titlesService } from 'src/services/titlesService';
import { userService } from 'src/services/userService';

const router    = useRouter();
const authStore = useAuthStore();

const titulos        = ref<TituloDTO[]>([]);
const titulosLoading = ref(true);
const editing        = ref(false);
const saving         = ref(false);
const saveError      = ref('');

const form = ref({
  nome:           authStore.user?.nome ?? '',
  telefone:       authStore.user?.telefone ?? '',
  dataNascimento: authStore.user?.dataNascimento?.slice(0, 10) ?? '',
});

const initials = computed(() => {
  const nome  = authStore.user?.nome ?? '';
  const parts = nome.trim().split(' ').filter(Boolean);
  if (parts.length >= 2) return ((parts[0]![0] ?? '') + (parts[parts.length - 1]![0] ?? '')).toUpperCase();
  return nome.slice(0,2).toUpperCase();
});

onMounted(async () => {
  try   { titulos.value = await titlesService.getActive(); }
  finally { titulosLoading.value = false; }
});

function toggleEdit() {
  if (editing.value) {
    form.value = {
      nome:           authStore.user?.nome ?? '',
      telefone:       authStore.user?.telefone ?? '',
      dataNascimento: authStore.user?.dataNascimento?.slice(0, 10) ?? '',
    };
  }
  saveError.value = '';
  editing.value   = !editing.value;
}

async function saveProfile() {
  saveError.value = '';
  saving.value    = true;
  try {
    const payload: Parameters<typeof userService.updateProfile>[0] = {};
    if (form.value.nome)           payload.nome           = form.value.nome;
    if (form.value.telefone)       payload.telefone       = form.value.telefone;
    if (form.value.dataNascimento) payload.dataNascimento = form.value.dataNascimento;
    const updated = await userService.updateProfile(payload);
    authStore.user = updated;
    localStorage.setItem('et_user', JSON.stringify(updated));
    editing.value = false;
  } catch {
    saveError.value = 'Não foi possível guardar as alterações. Tenta novamente...';
  } finally { saving.value = false; }
}

function logout() {
  authStore.logout();
  void router.replace('/login');
}
</script>

<style scoped>
.perfil-page { background: var(--et-bg); min-height: 100dvh; }

/* avatar */
.avatar-section { display: flex; flex-direction: column; align-items: center; padding: 20px 0 8px; }
.avatar-circle  { width: 72px; height: 72px; border-radius: 50%; background: var(--et-blue); display: flex; align-items: center; justify-content: center; margin-bottom: 12px; }
.avatar-initials { color: white; font-size: 26px; font-weight: 800; }
.avatar-name  { font-size: 18px; font-weight: 700; color: var(--et-text); margin: 0; }
.avatar-email { font-size: 13px; color: var(--et-subtext); margin: 4px 0 0; }

/* section card */
.section-card { background: white; border-radius: 14px; padding: 16px; }
.section-card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.section-card-title  { font-size: 14px; font-weight: 700; color: var(--et-text); }

/* fields */
.field-row   { display: flex; align-items: center; justify-content: space-between; padding: 10px 0; gap: 12px; }
.field-value { font-size: 14px; color: var(--et-text); text-align: right; }
.field-input {
  flex: 1; text-align: right; border: none; outline: none;
  border-bottom: 1.5px solid var(--et-blue); font-size: 14px;
  color: var(--et-text); background: transparent; padding: 2px 0; font-family: 'Roboto', sans-serif;
}
.field-divider { height: 1px; background: var(--et-border); }

/* títulos */
.titulo-row      { display: flex; align-items: center; gap: 12px; padding: 10px 0; }
.titulo-icon-wrap { width: 36px; height: 36px; background: #eff6ff; border-radius: 9px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.titulo-info     { flex: 1; min-width: 0; }
.titulo-tipo     { font-size: 13px; font-weight: 600; color: var(--et-text); }
.titulo-zones-row { display: flex; flex-wrap: wrap; gap: 4px; margin-top: 4px; }
.titulo-side     { text-align: right; flex-shrink: 0; }
.titulo-validade { display: block; font-size: 11px; color: var(--et-subtext); }
.titulo-saldo    { display: block; font-size: 12px; font-weight: 700; color: var(--et-blue); }
.empty-titulos   { font-size: 13px; color: var(--et-subtext); padding: 8px 0; }

/* logout */
.logout-btn {
  background: #fee2e2 !important; color: #dc2626 !important;
  border-radius: 12px !important; height: 48px;
  font-size: 13px !important; font-weight: 700 !important; letter-spacing: 0.5px;
}
</style>