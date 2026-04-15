<template>
  <div class="qr-scanner-wrap">

    <div class="qr-video-area">
      <video ref="videoRef" class="qr-video" />
      <div class="qr-overlay">
        <div class="qr-target">
          <span class="corner tl" />
          <span class="corner tr" />
          <span class="corner bl" />
          <span class="corner br" />
        </div>
      </div>
    </div>

    <div class="qr-manual">
      <div class="qr-input-row">
        <input
            v-model="manualCode"
            class="qr-text-input"
            placeholder="ou digita o código manualmente..."
            @keyup.enter="submitManual"
        />
        <button class="qr-send-btn" :disabled="!manualCode.trim()" @click="submitManual">
          <q-icon name="arrow_forward" size="18px" />
        </button>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue';
import QrScanner from 'qr-scanner';

const emit = defineEmits<{ (e: 'scan', code: string): void }>();

const videoRef = ref<HTMLVideoElement | null>(null);
const manualCode = ref('');
let scanner: QrScanner | null = null;

onMounted(async () => {
  if (!videoRef.value) return;
  const hasCamera = await QrScanner.hasCamera().catch(() => false);
  if (!hasCamera) return;

  scanner = new QrScanner(
      videoRef.value,
      (result) => {
        stopScanner();
        emit('scan', result.data);
      },
      {
        returnDetailedScanResult: true,
        highlightScanRegion: false,
        highlightCodeOutline: false,
      },
  );
  await scanner.start().catch(() => { /* câmara recusada */ });
});

onBeforeUnmount(() => stopScanner());

function stopScanner() {
  scanner?.stop();
  scanner?.destroy();
  scanner = null;
}

function submitManual() {
  const code = manualCode.value.trim();
  if (!code || code.length > 100) return;
  emit('scan', code);
}

defineExpose({ stop: stopScanner });
</script>

<style scoped>
.qr-scanner-wrap {
  width: 100%;
  border-radius: 16px;
  overflow: hidden;
  background: #111;
}

/* container isolado só para o vídeo + overlay */
.qr-video-area {
  position: relative;
}

.qr-video {
  width: 100%;
  display: block;
  min-height: 260px;
  object-fit: cover;
  background: #1a1a2e;
}

/* overlay cobre só a área do vídeo */
.qr-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  pointer-events: none;
}

/* box-shadow cria o escurecimento quadrado em torno do alvo */
.qr-target {
  position: relative;
  height: 100%;
  aspect-ratio: 1 / 1;
  border-radius: 6px;
  box-shadow: 0 0 0 9999px rgba(0, 0, 0, 0.5);
}

.corner {
  position: absolute;
  width: 28px;
  height: 28px;
  border-color: #fbbf24;
  border-style: solid;
}
.corner.tl { top: 0;    left: 0;   border-width: 3px 0 0 3px; border-radius: 4px 0 0 0; }
.corner.tr { top: 0;    right: 0;  border-width: 3px 3px 0 0; border-radius: 0 4px 0 0; }
.corner.bl { bottom: 0; left: 0;   border-width: 0 0 3px 3px; border-radius: 0 0 0 4px; }
.corner.br { bottom: 0; right: 0;  border-width: 0 3px 3px 0; border-radius: 0 0 4px 0; }

/* input manual */
.qr-manual {
  background: #1a1a2e;
  padding: 10px 12px;
}

.qr-input-row {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.07);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 10px;
  padding: 8px 10px;
}

.qr-text-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  color: rgba(255, 255, 255, 0.8);
  font-size: 13px;
  font-family: 'Roboto', sans-serif;
  min-width: 0;
}

.qr-text-input::placeholder {
  color: rgba(255, 255, 255, 0.65);
}

.qr-send-btn {
  background: #5b8ff9;
  border: none;
  border-radius: 7px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.15s;
  flex-shrink: 0;
  color: white;
}

.qr-send-btn:disabled {
  background: rgba(91, 143, 249, 0.3);
  cursor: default;
}

.qr-send-btn:not(:disabled):active {
  background: #3d6fd9;
}
</style>