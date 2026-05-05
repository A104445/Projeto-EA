<template>
  <q-page class="perfil-page">

    <div class="perfil-header" />

    <div class="page-content">

      <!-- avatar + info -->
      <div class="avatar-section">
        <div class="avatar-circle">
          <span class="avatar-initials">{{ initials }}</span>
        </div>
        <p class="user-name">{{ authStore.user?.nome }}</p>
        <p class="user-meta">
          {{ authStore.user?.email }}
          <template v-if="authStore.user?.telefone"> | {{ authStore.user.telefone }}</template>
        </p>
      </div>

      <!-- menu -->
      <div class="menu-section">
        <div class="menu-item" @click="$router.push('/app/perfil/editar')">
          <q-icon name="badge" size="22px" color="grey-6" />
          <span class="menu-label">Alterar Informações Pessoais</span>
          <q-icon name="chevron_right" size="18px" color="grey-4" />
        </div>
        <div class="menu-item">
          <q-icon name="help_outline" size="22px" color="grey-6" />
          <span class="menu-label">Suporte</span>
          <q-icon name="chevron_right" size="18px" color="grey-4" />
        </div>
      </div>

    </div>

    <!-- botão fixo no fundo -->
    <div class="bottom-bar">
      <q-btn
        unelevated
        class="logout-btn full-width"
        label="TERMINAR SESSÃO"
        icon="logout"
        @click="logout"
      />
    </div>

  </q-page>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from 'stores/auth';
const router    = useRouter();
const authStore = useAuthStore();
const initials = computed(() => {
  const nome  = authStore.user?.nome ?? '';
  const parts = nome.trim().split(' ').filter(Boolean);
  if (parts.length >= 2) return ((parts[0]![0] ?? '') + (parts[parts.length - 1]![0] ?? '')).toUpperCase();
  return nome.slice(0, 2).toUpperCase();
});

function logout() {
  authStore.logout();
  void router.replace('/login');
}
</script>

<style scoped>
.perfil-page { background: var(--et-bg); min-height: 100dvh; }

.perfil-header {
  background: linear-gradient(170deg, #5b87f7 0%, #93b6ff 100%);
  padding: 52px 20px 70px;
  border-radius: 0 0 50% 50% / 0 0 48px 48px;
}

.page-content {
  padding: 0 16px 100px;
  display: flex; flex-direction: column; gap: 16px;
}

/* ── avatar ── */
.avatar-section {
  display: flex; flex-direction: column; align-items: center;
  margin-top: -44px;
  padding-bottom: 8px;
}
.avatar-circle {
  width: 84px; height: 84px; border-radius: 50%;
  background: white;
  border: 4px solid white;
  box-shadow: 0 4px 16px rgba(0,0,0,0.12);
  display: flex; align-items: center; justify-content: center;
  margin-bottom: 12px;
}
.avatar-initials {
  font-size: 28px; font-weight: 800; color: var(--et-blue);
  font-family: 'Lexend', sans-serif;
}
.user-name {
  font-size: 20px; font-weight: 700; color: var(--et-text);
  margin: 0 0 4px;
}
.user-meta {
  font-size: 13px; color: var(--et-subtext); margin: 0; text-align: center;
}

/* ── menu ── */
.menu-section {
  background: white; border-radius: 16px;
  overflow: hidden; border: 1px solid var(--et-border);
}
.menu-item {
  display: flex; align-items: center; gap: 14px;
  padding: 16px;
  cursor: pointer; transition: background 0.15s;
  border-bottom: 1px solid var(--et-border);
}
.menu-item:last-child { border-bottom: none; }
.menu-item:active { background: #f9fafb; }
.menu-label { flex: 1; font-size: 15px; font-weight: 500; color: var(--et-text); }

/* ── botão fixo ── */
.bottom-bar {
  position: fixed;
  bottom: 60px;
  left: max(0px, calc(50vw - 215px));
  width: min(430px, 100vw);
  padding: 12px 16px 16px;
  background: var(--et-bg);
  border-top: 1px solid var(--et-border);
  z-index: 10;
}
.logout-btn {
  background: #fee2e2 !important;
  color: #dc2626 !important;
  border-radius: 12px !important;
  height: 48px;
  font-family: 'Roboto', sans-serif !important;
  font-size: 13px !important;
  font-weight: 700 !important;
  letter-spacing: 0.5px;
  box-shadow: none !important;
}
</style>