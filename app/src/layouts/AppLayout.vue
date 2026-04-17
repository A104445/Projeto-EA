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
        <q-tab name="home" icon="home" label="Início" @click="$router.push('/app')" />
        <q-tab
          name="bilhetes"
          icon="confirmation_number"
          label="Bilhetes"
          @click="$router.push('/app/bilhetes')"
        />
        <q-tab
          name="percursos"
          icon="map"
          label="Percursos"
          @click="$router.push('/app/percursos')"
        />
        <q-tab name="perfil" icon="person" label="Perfil" @click="$router.push('/app/perfil')" />
      </q-tabs>
    </q-footer>
  </q-layout>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();

const tabMap: Record<string, string> = {
  '/app': 'home',
  '/app/bilhetes': 'bilhetes',
  '/app/percursos': 'percursos',
  '/app/perfil': 'perfil',
};

const activeTab = ref(tabMap[route.path] ?? 'home');

watch(
  () => route.path,
  (p) => {
    activeTab.value = tabMap[p] ?? 'home';
  },
);
</script>
