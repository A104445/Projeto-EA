<template>
  <q-dialog :model-value="modelValue" position="bottom"
            @update:model-value="$emit('update:modelValue', $event)">
    <q-card class="bottom-sheet">
      <q-card-section class="text-center q-pt-lg q-pb-sm">
        <q-icon :name="icon" size="56px" :color="iconColor" />
        <p class="text-h6 q-mt-md q-mb-xs">{{ title }}</p>
        <p class="text-grey-6 text-caption">{{ message }}</p>
      </q-card-section>
      <q-card-actions class="sheet-actions">
        <q-btn unelevated class="et-btn-primary full-width" label="OK" style="height:48px"
               @click="$emit('update:modelValue', false)" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
defineProps<{
  modelValue: boolean;
  icon: string;
  iconColor: string;
  title: string;
  message: string;
}>();
defineEmits<{ 'update:modelValue': [value: boolean] }>();
</script>