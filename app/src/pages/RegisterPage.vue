<template>
  <q-page class="auth-page">
    <div class="auth-container">

      <q-btn flat round icon="arrow_back" color="grey-7" class="back-btn" @click="$router.back()" />

      <div class="auth-header">
        <h1 class="auth-title">Registar</h1>
        <p class="auth-subtitle">Crie uma conta e agilize as suas viagens!</p>
      </div>

      <q-form class="auth-form" @submit.prevent="handleRegister">
        <div class="field-group">
          <label class="field-label">NOME</label>
          <q-input
            v-model="nome"
            outlined
            placeholder="o teu nome"
            :rules="[(v) => !!v || 'Campo obrigatório!']"
            class="et-input"
            no-error-icon
          />
        </div>

        <div class="field-group">
          <label class="field-label">E-MAIL</label>
          <q-input
            v-model="email"
            outlined
            placeholder="o teu endereço de e-mail"
            type="email"
            :rules="[(v) => !!v || 'Campo obrigatório!', (v) => /.+@.+/.test(v) || 'E-mail inválido!']"
            class="et-input"
            no-error-icon
          />
        </div>

        <div class="field-group">
          <label class="field-label">PALAVRA-PASSE</label>
          <q-input
            v-model="password"
            outlined
            placeholder="mínimo 6 caracteres"
            :type="showPwd ? 'text' : 'password'"
            :rules="[(v) => !!v || 'Campo obrigatório!', (v) => v.length >= 6 || 'mínimo 6 caracteres']"
            class="et-input"
            no-error-icon
          >
            <template #append>
              <q-icon
                :name="showPwd ? 'visibility_off' : 'visibility'"
                class="cursor-pointer text-grey-5"
                @click="showPwd = !showPwd"
              />
            </template>
          </q-input>
        </div>

        <q-banner v-if="error" rounded class="bg-red-1 text-red-8 q-mb-md" dense>
          {{ error }}
        </q-banner>

        <q-btn
          unelevated
          type="submit"
          class="et-btn-primary full-width auth-submit-btn"
          label="CRIAR CONTA"
          :loading="loading"
        />
      </q-form>

      <!-- social login -->
      <div class="social-section">
        <div class="et-divider-text">Ou continue com</div>
        <div class="social-buttons">
          <q-btn round flat bordered class="google-btn">
            <img src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg" alt="Google" width="20" />
          </q-btn>
        </div>
      </div>

      <!-- login link -->
      <p class="switch-link">
        Já tens uma conta?
        <q-btn flat no-caps dense label="Iniciar sessão" color="blue-6" @click="$router.push('/login')" />
      </p>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from 'stores/auth';

const router = useRouter();
const authStore = useAuthStore();

const nome = ref('');
const email = ref('');
const password = ref('');
const showPwd = ref(false);
const loading = ref(false);
const error = ref('');

async function handleRegister() {
  error.value = '';
  loading.value = true;
  try {
    await authStore.register(nome.value, email.value, password.value);
    await router.push('/app');
  } catch (err: unknown) {
    const e = err as { response?: { status?: number; data?: { error?: string } } };
    if (e?.response?.status === 400 && e?.response?.data?.error?.includes('já registado')) {
      error.value = 'Já existe uma conta com este e-mail!';
    } else {
      error.value = 'Ocorreu um erro ao criar a conta. Tente novamente...';
    }
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100dvh;
  background: white;
}

.auth-container {
  padding: 16px 24px 40px;
  max-width: 400px;
  margin: 0 auto;
}

.back-btn {
  margin-left: -8px;
  margin-bottom: 8px;
}

.auth-header {
  margin-bottom: 32px;
}

.auth-title {
  font-size: 28px;
  font-weight: 800;
  color: var(--et-blue);
  margin: 0 0 6px;
  font-style: italic;
}

.auth-subtitle {
  font-size: 15px;
  color: var(--et-subtext);
  margin: 0;
  text-align: center;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 8px;
}

.field-label {
  font-size: 11px;
  font-weight: 700;
  color: var(--et-subtext);
  letter-spacing: 0.5px;
}

.auth-submit-btn {
  height: 50px;
  margin-top: 12px;
}

.social-section {
  margin-top: 28px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.social-buttons {
  display: flex;
  gap: 12px;
}

.google-btn {
  border: 1px solid #e5e7eb !important;
  width: 48px;
  height: 48px;
}

.switch-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: var(--et-subtext);
}
</style>
