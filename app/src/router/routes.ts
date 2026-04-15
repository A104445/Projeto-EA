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
        path: 'pagamentos',
        component: () => import('pages/PaymentsPage.vue'),
      },
      {
        path: 'checkin',
        component: () => import('pages/CheckInPage.vue'),
      },
      {
        path: 'percursos',
        component: () => import('pages/PlaceholderPage.vue'),
        props: { icon: 'map', title: 'Percursos' },
      },
      {
        path: 'perfil',
        component: () => import('pages/PlaceholderPage.vue'),
        props: { icon: 'person', title: 'Perfil' },
      },
    ],
  },

  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue'),
  },
];

export default routes;
