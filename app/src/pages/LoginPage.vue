<template>
  <q-page class="auth-page">
    <div class="auth-container">

      <q-btn flat round icon="arrow_back" color="grey-7" class="back-btn" @click="$router.back()" />

      <div class="auth-header">
        <h1 class="auth-title">Iniciar Sessão</h1>
        <p class="auth-subtitle">Bem-vindo de volta!</p>
      </div>

      <q-form class="auth-form" @submit.prevent="handleLogin">
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
            placeholder="a tua palavra-passe"
            :type="showPwd ? 'text' : 'password'"
            :rules="[(v) => !!v || 'Campo obrigatório!']"
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

        <div class="forgot-link">
          <q-btn flat no-caps dense label="Esqueci-me da palavra-passe..." color="blue-6" />
        </div>

        <q-banner v-if="error" rounded class="bg-red-1 text-red-8 q-mb-md" dense>
          {{ error }}
        </q-banner>

        <q-btn
          unelevated
          type="submit"
          class="et-btn-primary full-width auth-submit-btn"
          label="INICIAR SESSÃO"
          :loading="loading"
        />
      </q-form>

      <!-- social login (isto nem deve ser para implementar) -->
      <div class="social-section">
        <div class="et-divider-text">Ou continue com</div>
        <div class="social-buttons">
          <q-btn round flat bordered class="google-btn">
            <img src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg" alt="Google" width="20" />
          </q-btn>
        </div>
      </div>

      <!-- register link -->
      <p class="switch-link">
        Não tens conta?
        <q-btn flat no-caps dense label="Registar" color="blue-6" @click="$router.push('/register')" />
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

const email = ref('');
const password = ref('');
const showPwd = ref(false);
const loading = ref(false);
const error = ref('');

async function handleLogin() {
  error.value = '';
  loading.value = true;
  try {
    await authStore.login(email.value, password.value);
    await router.push('/app');
  } catch {
    error.value = 'E-mail ou palavra-passe incorretos!';
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

.forgot-link {
  display: flex;
  justify-content: flex-end;
  margin-top: -4px;
  margin-bottom: 12px;
}

.auth-submit-btn {
  height: 50px;
  margin-top: 4px;
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
