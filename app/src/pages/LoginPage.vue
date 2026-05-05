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
            :rules="[
              (v) => !!v || 'Campo obrigatório!',
              (v) => /.+@.+/.test(v) || 'E-mail inválido!',
            ]"
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
          :disable="googleLoading"
        />
      </q-form>

      <div class="social-section">
        <div class="et-divider-text">Ou continue com</div>
        <div class="social-buttons">
          <q-btn
            round
            flat
            bordered
            class="google-btn"
            :loading="googleLoading"
            :disable="loading || !isReady"
            @click="() => login()"
          >
            <img
              src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg"
              alt="Google"
              width="20"
            />
          </q-btn>
        </div>
      </div>

      <p class="switch-link">
        Não tens conta?
        <q-btn
          flat
          no-caps
          dense
          label="Registar"
          color="blue-6"
          @click="$router.push('/register')"
        />
      </p>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { useAuthStore } from 'stores/auth';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import {
  useCodeClient,
  type ImplicitFlowSuccessResponse,
} from 'vue3-google-signin';

const router = useRouter();
const authStore = useAuthStore();

const GOOGLE_SCOPE = 'openid email profile';

const email = ref('');
const password = ref('');
const showPwd = ref(false);
const loading = ref(false);
const googleLoading = ref(false);
const error = ref('');
const hasGoogleClientId = Boolean(import.meta.env.VITE_GOOGLE_CLIENT_ID?.trim());

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

const handleOnSuccess = async (response: ImplicitFlowSuccessResponse) => {
  googleLoading.value = true;
  try {
    await authStore.loginWithGoogle(response.code);
    await router.push('/app');
  } catch {
    error.value = 'Erro ao fazer login com o Google!';
  } finally {
    googleLoading.value = false;
  }
};

const handleOnError = () => {
  googleLoading.value = false;
  error.value = 'Erro ao fazer login com o Google!';
};

const googleClient = hasGoogleClientId
  ? useCodeClient({
      onSuccess: (response) => {
        void handleOnSuccess(response);
      },
      onError: handleOnError,
      scope: GOOGLE_SCOPE,
    })
  : { isReady: ref(false), login: () => undefined };

const { isReady, login } = googleClient;
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
  color: var(--et-blue) !important;
}

.auth-header {
  margin-bottom: 32px;
  text-align: center;
}

.auth-title {
  font-family: 'Lexend', sans-serif;
  font-size: 35px;
  font-weight: 800;
  color: #719BF5;
  margin: 0 0 6px;
  text-align: center;
  line-height: 44px;
}

.auth-subtitle {
  font-family: 'Jaldi', sans-serif;
  font-size: 20px;
  font-weight: 400;
  color: #5D5D5D;
  opacity: 0.8;
  margin: 0;
  text-align: center;
  line-height: 34px;
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
  font-family: 'Jaldi', sans-serif;
  font-size: 15px;
  font-weight: 400;
  color: #719BF5;
  opacity: 0.8;
  letter-spacing: 0.5px;
}

.forgot-link {
  display: flex;
  justify-content: flex-end;
  margin-top: -4px;
  margin-bottom: 12px;
}

.forgot-link :deep(.q-btn__content) {
  font-family: 'Jaldi', sans-serif;
  font-size: 15px;
  font-weight: 400;
  color: #719BF5;
}

.auth-submit-btn {
  height: 55px;
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
  border: 1px solid #7E7E7E !important;
  width: 60px !important;
  height: 40px !important;
  border-radius: 100px !important;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.25) !important;
}

.switch-link {
  text-align: center;
  margin-top: 20px;
  font-family: 'Jaldi', sans-serif;
  font-size: 14px;
  color: var(--et-subtext);
}

.et-divider-text {
  font-family: 'Jaldi', sans-serif;
  font-size: 15px;
  font-weight: 700;
  color: #719BF5;
  width: 100%;
}

.et-divider-text::before,
.et-divider-text::after {
  background: #719BF5;
}
</style>
