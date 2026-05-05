<template>
  <q-layout view="lHh lpr fFf">
    <q-page-container style="padding-bottom: 60px">
      <router-view />
    </q-page-container>

    <q-footer bordered style="background: white; border-top: 1px solid #e5e7eb">
      <q-tabs
        v-model="activeTab"
        no-caps
        active-color="blue-6"
        indicator-color="transparent"
        class="text-grey-6"
        style="height: 60px"
      >
        <q-tab name="home" @click="$router.push('/app')">
          <House :size="22" :stroke-width="1.75" />
          <div class="nav-label">Início</div>
        </q-tab>
        <q-tab name="bilhetes" @click="$router.push('/app/bilhetes')">
          <ShoppingCart :size="22" :stroke-width="1.75" />
          <div class="nav-label">Bilheteira</div>
        </q-tab>
        <q-tab name="percursos" @click="$router.push('/app/percursos')">
          <Map :size="22" :stroke-width="1.75" />
          <div class="nav-label">Percursos</div>
        </q-tab>
        <q-tab name="perfil" @click="$router.push('/app/perfil')">
          <UserRound :size="22" :stroke-width="1.75" />
          <div class="nav-label">Perfil</div>
        </q-tab>
      </q-tabs>
    </q-footer>
  </q-layout>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { House, ShoppingCart, Map, UserRound } from 'lucide-vue-next';

const route = useRoute();

function pathToTab(p: string): string {
  if (p.startsWith('/app/bilhetes')) return 'bilhetes';
  if (p.startsWith('/app/percursos')) return 'percursos';
  if (p.startsWith('/app/perfil'))  return 'perfil';
  return 'home';
}

const activeTab = ref(pathToTab(route.path));

watch(() => route.path, (p) => { activeTab.value = pathToTab(p); });
</script>

<style scoped>
.nav-label {
  font-family: 'Jaldi',sans-serif;
  font-size: 13px;
  font-weight: 400;
  line-height: 1;
  margin-top: 2px;
}
</style>
