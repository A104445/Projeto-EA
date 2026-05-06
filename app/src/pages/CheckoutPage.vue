<template>
  <q-page class="checkout-page">

    <PageHeader title="Bilheteira" :show-back="true" />

    <div class="page-content">

      <div class="resumo-card">
        <div class="resumo-main">{{ resumoTitle }}</div>
        <div class="resumo-sub">{{ resumoSub }}</div>

        <div v-if="cart.zonaNomes.length > 0" class="zona-pills">
          <span
            v-for="nome in cart.zonaNomes" :key="nome"
            class="zona-pill"
            :style="{ background: zonaColor(nome) }"
          >{{ zonaBadgeLabel(nome) }}</span>
        </div>
      </div>

      <q-banner v-if="error" rounded dense class="bg-red-1 text-red-8">
        {{ error }}
      </q-banner>
    </div>

    <!-- barra fixa com total + seleção de pagamento + botão -->
    <div class="bottom-bar">
      <div class="total-row q-mb-sm">
        <span class="total-label">Total a pagar:</span>
        <span class="total-chip">{{ formatPrice(total) }}</span>
      </div>
      <div class="payment-options">
        <div
          v-for="m in PAYMENT_METHODS" :key="m.key"
          class="payment-option"
          :class="{ 'payment-option--selected': selectedPayment === m.key }"
          @click="selectedPayment = m.key"
        >
          <div class="payment-icon-wrap">
            <img v-if="m.img" :src="m.img" :alt="m.label" width="20" height="20" />
            <q-icon v-else :name="m.icon" size="20px" :color="m.color" />
          </div>
          <span class="payment-label">{{ m.label }}</span>
          <div class="radio-dot" :class="{ 'radio-dot--active': selectedPayment === m.key }" />
        </div>
      </div>

      <q-btn
        unelevated
        class="et-btn-primary full-width q-mt-sm"
        label="Finalizar Compra"
        :disable="!selectedPayment || loading"
        :loading="loading"
        @click="confirmar"
      />
    </div>

    <ResultDialog
      v-model="success"
      icon="check_circle"
      icon-color="green-6"
      title="Compra concluída!"
      message="O teu título foi adicionado à conta."
      @hide="router.replace('/app/bilhetes')"
    />
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import PageHeader from 'components/PageHeader.vue';
import ResultDialog from 'components/ResultDialog.vue';
import { useCartStore } from 'stores/cart';
import { titlesService } from 'src/services/titlesService';
import { zonaColor, zonaBadgeLabel } from 'src/utils/zones';

const PRECO_BILHETE       = 1.20;
const PRECO_PASSE_ZONA    = 15.00;
const PRECO_PACOTE_VIAGEM = 0.90;

const PAYMENT_METHODS = [
  {
    key: 'googlepay',
    img: 'https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg',
    icon: '', color: '',
    label: 'Google Pay',
  },
  {
    key: 'card',
    img: '', icon: 'credit_card', color: 'red-6',
    label: 'Cartão de Débito ou Crédito',
  },
];

const router = useRouter();
const cart   = useCartStore();

const selectedPayment = ref('');
const loading         = ref(false);
const error           = ref('');
const success         = ref(false);

const total = computed(() => {
  if (cart.tipo === 'bilhete') return PRECO_BILHETE;
  if (cart.tipo === 'passe')   return PRECO_PASSE_ZONA * cart.zonaIDs.length;
  return PRECO_PACOTE_VIAGEM * cart.numViagens;
});

const resumoTitle = computed(() => {
  if (cart.tipo === 'bilhete') return `Bilhete → ${cart.carreiraNome}`;
  if (cart.tipo === 'passe')   return 'Passe Anual';
  return `Pacote de ${cart.numViagens} Viagens`;
});

const resumoSub = computed(() => {
  if (cart.tipo === 'bilhete') return `Linha ${cart.carreiraNum}`;
  return '';
});

function formatPrice(v: number) {
  return v.toFixed(2).replace('.', ',') + '€';
}

async function confirmar() {
  error.value   = '';
  loading.value = true;
  try {
    if (cart.tipo === 'bilhete') {
      await titlesService.buyBilhete(cart.carreiraID!);
    } else if (cart.tipo === 'passe') {
      await titlesService.buyPasseAnual(cart.zonaIDs);
    } else {
      await titlesService.buyPacote(cart.numViagens, cart.zonaIDs);
    }
    success.value = true;
  } catch {
    error.value = 'Ocorreu um erro ao processar o pagamento. Tente novamente...';
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.checkout-page { background: var(--et-bg); min-height: 100dvh; }

.page-content {
  padding: 16px 16px 260px;
  display: flex; flex-direction: column; gap: 16px;
}

.resumo-title { font-size: 18px; font-weight: 700; color: var(--et-text); }

.resumo-card {
  background: white; border-radius: 14px; padding: 16px;
  border: 1px solid var(--et-border);
  display: flex; flex-direction: column; gap: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.07);
}
.resumo-main { font-size: 15px; font-weight: 600; color: var(--et-text); }
.resumo-sub  { font-size: 13px; color: var(--et-subtext); }

.zona-pills { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 4px; }
.zona-pill  {
  padding: 4px 12px; border-radius: 20px;
  font-size: 12px; font-weight: 700; color: white;
}

.total-row   { display: flex; align-items: center; justify-content: space-between; }
.total-label { font-size: 15px; font-weight: 500; color: var(--et-text); }
.total-chip  {
  background: #f3f4f6; border-radius: 20px; padding: 6px 18px;
  font-size: 18px; font-weight: 700; color: var(--et-text);
}

/* ── barra fixa ── */
.bottom-bar {
  position: fixed;
  bottom: 60px;
  left: max(0px, calc(50vw - 215px));
  width: min(430px, 100vw);
  background: white;
  padding: 12px 16px 16px;
  border-top: 1px solid var(--et-border);
  z-index: 10;
}

.payment-options { display: flex; flex-direction: column; gap: 8px; }

.payment-option {
  display: flex; align-items: center; gap: 12px;
  background: #f9fafb; border-radius: 14px; padding: 12px 14px;
  border: 1.5px solid transparent;
  cursor: pointer; transition: border-color 0.15s, background 0.15s;
}
.payment-option:active { background: #f3f4f6; }
.payment-option--selected {
  border-color: #719BF5;
  background: #eff6ff;
}
.payment-icon-wrap {
  width: 28px; height: 28px; border-radius: 50%;
  background: white; display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.payment-label { flex: 1; font-size: 14px; font-weight: 500; color: var(--et-text); }

.radio-dot {
  width: 18px; height: 18px; border-radius: 50%;
  border: 2px solid #d1d5db;
  flex-shrink: 0; transition: border-color 0.15s, background 0.15s;
}
.radio-dot--active {
  border-color: #719BF5;
  background: #719BF5;
  box-shadow: inset 0 0 0 3px white;
}

</style>