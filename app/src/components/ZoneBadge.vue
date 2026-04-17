<template>
  <span class="zone-badge" :style="{ background: zonaColor(zona) }">{{ zonaBadgeLabel(zona) }}</span>
</template>

<script setup lang="ts">
import { zonaColor, zonaBadgeLabel } from 'src/utils/zones';
defineProps<{ zona: string }>();
</script>

<style scoped>
.zone-badge {
  font-size: 10px;
  font-weight: 700;
  color: white;
  padding: 2px 8px;
  border-radius: 20px;
  letter-spacing: 0.3px;
}
</style>