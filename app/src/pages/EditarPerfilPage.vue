<template>
  <q-page class="editar-page">

    <PageHeader title="Perfil" :show-back="true" />

    <!-- formulário -->
    <div class="form-card">
      <q-input
        v-model="form.nome"
        outlined
        label="Nome"
        class="form-input"
        :bg-color="'white'"
      />
      <q-input
        :model-value="authStore.user?.email"
        outlined
        label="E-mail"
        readonly
        class="form-input"
        :bg-color="'grey-1'"
      />
      <q-input
        v-model="form.telefone"
        outlined
        label="Telefone"
        type="tel"
        class="form-input"
        :bg-color="'white'"
      />
      <q-input
        v-model="form.password"
        outlined
        label="Palavra-passe"
        type="password"
        class="form-input"
        :bg-color="'white'"
        placeholder="••••••••"
      />

      <q-banner v-if="saveError" rounded dense class="bg-red-1 text-red-8 q-mt-sm">
        {{ saveError }}
      </q-banner>
    </div>

    <div class="form-footer">
      <q-btn
        unelevated
        class="et-btn-primary full-width"
        style="height: 52px"
        label="Guardar alterações"
        :loading="saving"
        @click="saveProfile"
      />
    </div>

  </q-page>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from 'stores/auth';
import PageHeader from 'components/PageHeader.vue';
import { userService } from 'src/services/userService';

const router    = useRouter();
const authStore = useAuthStore();

const saving   = ref(false);
const saveError = ref('');

const form = ref({
  nome:      authStore.user?.nome      ?? '',
  telefone:  authStore.user?.telefone  ?? '',
  password:  '',
});

async function saveProfile() {
  saveError.value = '';
  saving.value    = true;
  try {
    const payload: Parameters<typeof userService.updateProfile>[0] = {};
    if (form.value.nome)     payload.nome     = form.value.nome;
    if (form.value.telefone) payload.telefone = form.value.telefone;
    const updated = await userService.updateProfile(payload);
    authStore.user = updated;
    localStorage.setItem('et_user', JSON.stringify(updated));
    void router.back();
  } catch {
    saveError.value = 'Não foi possível guardar as alterações. Tenta novamente...';
  } finally {
    saving.value = false;
  }
}
</script>

<style scoped>
.editar-page { background: var(--et-bg); min-height: 100dvh; }

/* ── formulário ── */
.form-card {
  background: white;
  margin: 20px 16px 0;
  border-radius: 14px;
  padding: 20px 16px;
  display: flex; flex-direction: column; gap: 16px;
  border: 1px solid var(--et-border);
}

.form-input :deep(.q-field__control) {
  border-radius: 8px;
}
.form-input :deep(.q-field__label) {
  font-size: 13px;
  font-weight: 600;
  color: var(--et-subtext);
}

/* ── botão ── */
.form-footer {
  padding: 20px 16px;
}
</style>