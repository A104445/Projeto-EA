<template>
  <q-page class="splash-page">
    <!-- top section: logo
    (não está muito parecdio com o protótipo) -->
    <div class="splash-top">
      <div class="splash-icons">
        <q-icon name="directions_boat" size="36px" class="splash-icon icon-top-left" />
        <q-icon name="flight" size="32px" class="splash-icon icon-top-right" />
        <q-icon name="directions_bus" size="38px" class="splash-icon icon-bottom-left" />
        <q-icon name="directions_railway" size="30px" class="splash-icon icon-bottom-right" />
      </div>
      <div class="splash-logo">
        <span class="splash-logo-text">EasyTrip</span>
      </div>
      <p class="splash-tagline">viaje sem contratempos!</p>
    </div>

    <!-- bottom section: actions -->
    <div class="splash-bottom">
      <div class="splash-actions">
        <q-btn
          unelevated
          class="et-btn-primary splash-btn"
          label="iniciar sessão"
          @click="$router.push('/login')"
        />
        <q-btn
          unelevated
          class="et-btn-outline splash-btn"
          label="registar"
          @click="$router.push('/register')"
        />
      </div>
    </div>
  </q-page>
</template>

<style scoped>
.splash-page {
  display: flex;
  flex-direction: column;
  min-height: 100dvh;
  background: white;
  overflow: hidden;
}

.splash-top {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 40px 24px 60px;
}

.splash-icons {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.splash-icon {
  position: absolute;
  color: #d1d5db;
}

.icon-top-left {
  top: 15%;
  left: 12%;
  transform: rotate(-20deg);
}
.icon-top-right {
  top: 12%;
  right: 10%;
  transform: rotate(20deg);
}
.icon-bottom-left {
  bottom: 30%;
  left: 8%;
  transform: rotate(-10deg);
}
.icon-bottom-right {
  bottom: 28%;
  right: 10%;
  transform: rotate(15deg);
}

.splash-logo {
  position: relative;
  z-index: 1;
  margin-bottom: 16px;
}

.splash-logo-text {
  font-size: 42px;
  font-weight: 800;
  color: var(--et-blue);
  font-style: italic;
  letter-spacing: -0.5px;
}

.splash-tagline {
  color: #6b7280;
  font-size: 16px;
  margin: 0;
  position: relative;
  z-index: 1;
}

.splash-bottom {
  background: var(--et-dark);
  border-radius: 32px 32px 0 0;
  padding: 40px 24px 48px;
}

.splash-actions {
  display: flex;
  flex-direction: row;
  gap: 16px;
}

.splash-btn {
  flex: 1;
  height: 48px;
  font-size: 15px !important;
}
</style>
