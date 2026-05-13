import { expect, test } from '@playwright/test';
import { createApiState, mockApi } from './helpers/mockApi';
import { gotoApp, seedAuth } from './helpers/session';

test('navega entre tabs da app autenticada', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app');

  await page.getByRole('tab', { name: 'Bilheteira' }).click();
  await expect(page).toHaveURL(/#\/app\/bilhetes$/);
  await expect(page.getByRole('heading', { name: 'Bilheteira' })).toBeVisible();

  await page.getByRole('tab', { name: 'Percursos' }).click();
  await expect(page).toHaveURL(/#\/app\/percursos$/);
  await expect(page.getByRole('heading', { name: 'Percursos e Horários' })).toBeVisible();

  await page.getByRole('tab', { name: 'Perfil' }).click();
  await expect(page).toHaveURL(/#\/app\/perfil$/);
  await expect(page.getByRole('button', { name: /TERMINAR SESSÃO/ })).toBeVisible();

  await page.getByRole('tab', { name: 'Início' }).click();
  await expect(page).toHaveURL(/#\/app$/);
});

test('faz logout e volta ao login', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/perfil');
  await page.getByRole('button', { name: /TERMINAR SESSÃO/ }).click();

  await expect(page).toHaveURL(/#\/login$/);
  await expect
    .poll(async () =>
      page.evaluate(() => ({
        token: localStorage.getItem('et_token'),
        user: localStorage.getItem('et_user'),
      })),
    )
    .toEqual({ token: null, user: null });
});

test('redireciona para login quando a API responde 401', async ({ page }) => {
  const state = createApiState({
    endpointErrors: {
      'GET /api/tickets/mine': {
        status: 401,
        body: { message: 'Unauthorized' },
      },
    },
  });
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/bilhetes/meus');

  await expect(page).toHaveURL(/#\/login$/);
  await expect(page.getByRole('heading', { name: 'Iniciar Sessão' })).toBeVisible();
});
