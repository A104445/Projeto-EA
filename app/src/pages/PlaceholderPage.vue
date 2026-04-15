<template>
  <q-page class="placeholder-page">
    <div class="placeholder-content">
      <q-icon :name="icon" size="56px" color="grey-4" />
      <p class="placeholder-title">{{ title }}</p>
      <p class="placeholder-sub">em breve...</p>
    </div>
  </q-page>
</template>

<script setup lang="ts">
defineProps<{
  icon: string;
  title: string;
}>();
</script>

<style scoped>
.placeholder-page {
  min-height: 100dvh;
  background: var(--et-bg);
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-content {
  text-align: center;
  color: #9ca3af;
}

.placeholder-title {
  font-size: 18px;
  font-weight: 600;
  margin: 12px 0 4px;
  color: #6b7280;
}

.placeholder-sub {
  font-size: 14px;
  margin: 0;
}
</style>
