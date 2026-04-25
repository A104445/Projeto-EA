import { expect, test } from '@playwright/test';
import { createApiState, mockApi } from './helpers/mockApi';
import { gotoApp, seedAuth } from './helpers/session';

test('mostra a splash page com ações principais', async ({ page }) => {
  await gotoApp(page, '/');

  await expect(page.getByText('EasyTrip')).toBeVisible();
  await expect(page.getByRole('button', { name: 'iniciar sessão' })).toBeVisible();
  await expect(page.getByRole('button', { name: 'registar' })).toBeVisible();
});

test('redireciona utilizador não autenticado para login ao abrir /app', async ({ page }) => {
  await gotoApp(page, '/app');

  await expect(page).toHaveURL(/#\/login$/);
  await expect(page.getByRole('heading', { name: 'Iniciar Sessão' })).toBeVisible();
});

test('redireciona utilizador autenticado para /app ao abrir login', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page);
  await mockApi(page, state);

  await gotoApp(page, '/login');

  await expect(page).toHaveURL(/#\/app$/);
  await expect(page.getByText('Olá,')).toBeVisible();
});

test('mostra a página 404 em rota desconhecida', async ({ page }) => {
  await gotoApp(page, '/nao-existe');

  await expect(page.getByText('404')).toBeVisible();
  await expect(page.getByText('Oops. Nothing here...')).toBeVisible();
});
