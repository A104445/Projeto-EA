<template>
  <q-page class="perfil-page">
    <PageHeader title="Perfil" />

    <div class="page-content">
      <!-- avatar + nome -->
      <div class="avatar-section">
        <div class="avatar-circle">
          <span class="avatar-initials">{{ initials }}</span>
        </div>
        <p class="avatar-name">{{ authStore.user?.nome }}</p>
        <p class="avatar-email">{{ authStore.user?.email }}</p>
      </div>

      <!-- dados pessoais -->
      <div class="section-card">
        <div class="section-card-header">
          <span class="section-card-title">Dados pessoais</span>
          <q-btn flat dense no-caps label="Editar" color="blue-6" size="13px" @click="openEdit" />
        </div>

        <div class="field-row">
          <span class="field-label">NOME</span>
          <span class="field-value">{{ authStore.user?.nome || '—' }}</span>
        </div>
        <div class="field-divider" />
        <div class="field-row">
          <span class="field-label">EMAIL</span>
          <span class="field-value">{{ authStore.user?.email }}</span>
        </div>
        <div class="field-divider" />
        <div class="field-row">
          <span class="field-label">TELEMÓVEL</span>
          <span class="field-value">{{ authStore.user?.telefone || '—' }}</span>
        </div>
        <div class="field-divider" />
        <div class="field-row">
          <span class="field-label">DATA DE NASCIMENTO</span>
          <span class="field-value">{{ formatDate(authStore.user?.dataNascimento) }}</span>
        </div>
      </div>

      <!-- logout -->
      <q-btn unelevated class="logout-btn full-width" label="TERMINAR SESSÃO"
             icon="logout" @click="logout" />
    </div>

    <!-- editar perfil (bottom-sheet) -->
    <q-dialog v-model="editDialog" position="bottom">
      <q-card class="bottom-sheet">
        <q-card-section class="sheet-header">
          <span class="sheet-title">Editar perfil</span>
          <q-btn flat round dense icon="close" @click="editDialog = false" />
        </q-card-section>

        <q-card-section class="sheet-body">
          <div class="field-label q-mb-xs">NOME</div>
          <input v-model="form.nome" class="edit-input q-mb-md" placeholder="o teu nome" />

          <div class="field-label q-mb-xs">TELEMÓVEL</div>
          <input v-model="form.telefone" class="edit-input q-mb-md" placeholder="o teu número de telemóvel" type="tel" />

          <div class="field-label q-mb-xs">DATA DE NASCIMENTO</div>
          <input v-model="form.dataNascimento" class="edit-input q-mb-md" type="date" />

          <q-banner v-if="saveError" rounded dense class="bg-red-1 text-red-8">{{ saveError }}</q-banner>
        </q-card-section>

        <q-card-actions class="sheet-actions">
          <q-btn unelevated class="et-btn-primary full-width" label="GUARDAR"
                 :loading="saving" style="height: 48px" @click="saveProfile" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from 'stores/auth';
import PageHeader from 'components/PageHeader.vue';
import { formatDate } from 'src/utils/formatters';
import { userService } from 'src/services/userService';

const router    = useRouter();
const authStore = useAuthStore();

const editDialog = ref(false);
const saving     = ref(false);
const saveError  = ref('');

const form = ref({
  nome:           '',
  telefone:       '',
  dataNascimento: '',
});

const initials = computed(() => {
  const nome  = authStore.user?.nome ?? '';
  const parts = nome.trim().split(' ').filter(Boolean);
  if (parts.length >= 2) return ((parts[0]![0] ?? '') + (parts[parts.length - 1]![0] ?? '')).toUpperCase();
  return nome.slice(0, 2).toUpperCase();
});

function openEdit() {
  form.value = {
    nome:           authStore.user?.nome ?? '',
    telefone:       authStore.user?.telefone ?? '',
    dataNascimento: authStore.user?.dataNascimento?.slice(0, 10) ?? '',
  };
  saveError.value = '';
  editDialog.value = true;
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
    editDialog.value = false;
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

.avatar-section { display: flex; flex-direction: column; align-items: center; padding: 20px 0 8px; }
.avatar-circle  { width: 72px; height: 72px; border-radius: 50%; background: var(--et-blue); display: flex; align-items: center; justify-content: center; margin-bottom: 12px; }
.avatar-initials { color: white; font-size: 26px; font-weight: 800; }
.avatar-name  { font-size: 18px; font-weight: 700; color: var(--et-text); margin: 0; }
.avatar-email { font-size: 13px; color: var(--et-subtext); margin: 4px 0 0; }

.section-card { background: white; border-radius: 14px; padding: 16px; }
.section-card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.section-card-title  { font-size: 14px; font-weight: 700; color: var(--et-text); }

.field-row   { display: flex; align-items: center; justify-content: space-between; padding: 10px 0; gap: 12px; }
.field-value { font-size: 14px; color: var(--et-text); text-align: right; }
.field-divider { height: 1px; background: var(--et-border); }

.edit-input {
  width: 100%; display: block; border: 1.5px solid var(--et-border); border-radius: 8px;
  padding: 10px 12px; font-size: 14px; color: var(--et-text);
  background: white; outline: none; font-family: 'Roboto', sans-serif;
  transition: border-color 0.15s;
}
.edit-input:focus { border-color: var(--et-blue); }

.logout-btn {
  background: #fee2e2 !important; color: #dc2626 !important;
  border-radius: 12px !important; height: 48px;
  font-size: 13px !important; font-weight: 700 !important; letter-spacing: 0.5px;
}
</style>