<template>
  <div class="page-header" :class="{ 'page-header--back': showBack }">
    <q-btn v-if="showBack" flat round icon="arrow_back" color="white" @click="$router.back()" />
    <h1 class="page-title">{{ title }}</h1>
  </div>
</template>

<script setup lang="ts">
defineProps<{ title: string; showBack?: boolean }>();
</script>