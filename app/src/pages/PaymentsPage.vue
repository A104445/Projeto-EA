<template>
  <q-page class="bilheteira-page">

    <PageHeader title="Bilheteira" />

    <div class="page-content">
      <div class="section-label">O que pretendes comprar?</div>

      <div class="opcoes-list">
        <div class="opcao-card" @click="$router.push('/app/bilhetes/comprar/passe')">
          <div class="opcao-icon">
            <q-icon name="card_membership" size="26px" color="white" />
          </div>
          <div class="opcao-text">
            <span class="opcao-nome">Passe Anual</span>
          </div>
          <q-icon name="chevron_right" color="grey-4" size="20px" />
        </div>

        <div class="opcao-card" @click="$router.push('/app/bilhetes/comprar/pacote')">
          <div class="opcao-icon">
            <q-icon name="local_activity" size="26px" color="white" />
          </div>
          <div class="opcao-text">
            <span class="opcao-nome">Pacote de Viagens</span>
          </div>
          <q-icon name="chevron_right" color="grey-4" size="20px" />
        </div>

        <div class="opcao-card" @click="$router.push('/app/bilhetes/comprar/bilhete')">
          <div class="opcao-icon">
            <q-icon name="directions_bus" size="26px" color="white" />
          </div>
          <div class="opcao-text">
            <span class="opcao-nome">Bilhete Individual</span>
          </div>
          <q-icon name="chevron_right" color="grey-4" size="20px" />
        </div>
      </div>
    </div>

  </q-page>
</template>

<script setup lang="ts">
import PageHeader from 'components/PageHeader.vue';
</script>

<style scoped>
.bilheteira-page { background: var(--et-bg); min-height: 100dvh; }

.section-label {
  font-size: 13px; font-weight: 700; color: var(--et-subtext);
  letter-spacing: 0.5px; text-transform: uppercase; margin-bottom: 12px;
}

.opcoes-list { display: flex; flex-direction: column; gap: 10px; }

.opcao-card {
  background: white; border-radius: 16px; padding: 16px;
  display: flex; align-items: center; gap: 14px;
  cursor: pointer; transition: background 0.15s;
  border: 1px solid var(--et-border);
}
.opcao-card:active { background: #f9fafb; }

.opcao-icon {
  width: 48px; height: 48px; border-radius: 12px;
  background: #719BF5;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}

.opcao-text { flex: 1; display: flex; align-items: center; }
.opcao-nome { font-size: 15px; font-weight: 700; color: var(--et-text); }
</style>