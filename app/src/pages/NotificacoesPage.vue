<template>
  <q-page class="notif-page">
    <PageHeader title="Notificações" />

    <div class="page-content">
      <div v-if="!loading && notificacoes.length > 0" class="clear-row">
        <button class="clear-btn" @click="notificacoes = []; notificacoesStore.clear()">Limpar tudo</button>
      </div>

      <template v-if="loading">
        <div v-for="i in 3" :key="i" class="notif-card">
          <q-skeleton type="circle" size="40px" />
          <div style="flex:1">
            <q-skeleton type="text" width="70%" />
            <q-skeleton type="text" width="50%" style="margin-top:4px" />
          </div>
        </div>
      </template>

      <div v-else-if="notificacoes.length === 0" class="empty-state">
        <q-icon name="notifications_none" size="52px" color="grey-4" />
        <p>Sem notificações de momento.</p>
      </div>

      <div v-else>
        <div v-for="(n, i) in notificacoes" :key="i" class="notif-card">
          <div class="notif-icon-wrap" :class="iconBg(n)">
            <q-icon :name="iconName(n)" size="20px" color="white" />
          </div>
          <div class="notif-body">
            <span class="notif-label">{{ label(n) }}</span>
            <p class="notif-msg">{{ n.mensagem }}</p>
          </div>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import PageHeader from 'components/PageHeader.vue';
import type { NotificacaoDTO } from 'components/models';
import { notificacoesService } from 'src/services/notificacoesService';
import { useNotificacoesStore } from 'stores/notificacoes';

const notificacoes = ref<NotificacaoDTO[]>([]);
const loading = ref(true);
const notificacoesStore = useNotificacoesStore();

onMounted(async () => {
  try { notificacoes.value = await notificacoesService.getAll(); }
  finally { loading.value = false; }
});

function iconName(n: NotificacaoDTO) {
  if (n.tipo === 'SALDO_BAIXO') return 'wallet';
  if (n.tipo === 'VIAGEM_IRREGULAR') return 'warning';
  return 'event_busy';
}

function iconBg(n: NotificacaoDTO) {
  if (n.tipo === 'PASSE_EXPIRADO') return 'bg-red';
  if (n.tipo === 'SALDO_BAIXO') return 'bg-orange';
  if (n.tipo === 'VIAGEM_IRREGULAR') return 'bg-orange';
  return 'bg-yellow';
}


function label(n: NotificacaoDTO) {
  if (n.tipo === 'PASSE_EXPIRADO') return 'Passe expirado';
  if (n.tipo === 'PASSE_EXPIRANDO') return 'Passe a expirar';
  if (n.tipo === 'VIAGEM_IRREGULAR') return 'Viagem irregular';
  return 'Saldo baixo';
}
</script>

<style scoped>
.notif-page { background: var(--et-bg); min-height: 100dvh; }

.notif-card {
  background: white; border-radius: 14px; padding: 14px 16px;
  display: flex; align-items: flex-start; gap: 14px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.07);
}

.notif-icon-wrap {
  width: 40px; height: 40px; border-radius: 10px; flex-shrink: 0;
  display: flex; align-items: center; justify-content: center;
}
.bg-red    { background: #ef4444; }
.bg-orange { background: #f97316; }
.bg-yellow { background: #eab308; }

.notif-body { flex: 1; }
.notif-label { font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; color: var(--et-subtext); }
.notif-msg   { font-size: 14px; color: var(--et-text); margin: 2px 0 0; }

.clear-row { display: flex; justify-content: flex-end; }
.clear-btn {
  background: none; border: none; font-size: 13px; color: var(--et-subtext);
  cursor: pointer; text-decoration: underline; padding: 0;
}
.clear-btn:hover { color: var(--et-text); }

.empty-state {
  text-align: center; padding: 60px 20px; color: var(--et-subtext);
}
.empty-state p { margin: 12px 0 0; font-size: 15px; }
</style>
