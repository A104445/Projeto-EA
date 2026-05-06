import type { RouteRecordRaw } from 'vue-router';

declare module 'vue-router' {
  interface RouteMeta {
    requiresAuth?: boolean;
  }
}

const routes: RouteRecordRaw[] = [
  // auth routes (no bottom nav)
  {
    path: '/',
    component: () => import('layouts/AuthLayout.vue'),
    children: [
      { path: '', component: () => import('pages/SplashPage.vue') },
      { path: 'login', component: () => import('pages/LoginPage.vue') },
      { path: 'register', component: () => import('pages/RegisterPage.vue') },
    ],
  },

  // app routes (with bottom nav)
  {
    path: '/app',
    component: () => import('layouts/AppLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', component: () => import('pages/HomePage.vue') },
      {
        path: 'bilhetes',
        component: () => import('pages/PaymentsPage.vue'),
      },
      {
        path: 'bilhetes/meus',
        component: () => import('pages/MeusBilhetesPage.vue'),
      },
      {
        path: 'bilhetes/comprar/:tipo',
        component: () => import('pages/CompraTituloPage.vue'),
      },
      {
        path: 'bilhetes/checkout',
        component: () => import('pages/CheckoutPage.vue'),
      },
      {
        path: 'percursos',
        component: () => import('pages/PercursosPage.vue'),
      },
      {
        path: 'historico',
        component: () => import('pages/HistoricoPage.vue'),
      },
      {
        path: 'perfil',
        component: () => import('pages/PerfilPage.vue'),
      },
      {
        path: 'perfil/editar',
        component: () => import('pages/EditarPerfilPage.vue'),
      },
      {
        path: 'notificacoes',
        component: () => import('pages/NotificacoesPage.vue'),
      },
    ],
  },

  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue'),
  },
];

export default routes;
