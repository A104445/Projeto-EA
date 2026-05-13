import { expect, test } from '@playwright/test';
import { createApiState, mockApi } from './helpers/mockApi';
import { gotoApp, seedAuth } from './helpers/session';

test('mostra greeting, passe ativo e viagens recentes', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app');

  await expect(page.getByText('Olá, Joao!')).toBeVisible();
  await expect(page.locator('.pass-label')).toHaveText('passe ATIVO');
  await expect(page.locator('.pass-type')).toHaveText('Passe Anual');
  await expect(page.locator('.pass-zones-row').getByText('Zona A')).toBeVisible();
  await expect(page.locator('.pass-zones-row').getByText('Zona B')).toBeVisible();

  await expect(page.getByText('Viagens recentes')).toBeVisible();
  await expect(page.locator('.trip-name').filter({ hasText: 'Aeroporto' })).toBeVisible();
  await expect(page.locator('.trip-name').filter({ hasText: 'Universidade' })).toBeVisible();
});

test('mostra estados vazios quando não há títulos nem viagens', async ({ page }) => {
  const state = createApiState({
    titlesActive: [],
    tripsHistory: [],
    notifications: [],
  });
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app');

  await expect(page.getByText('Sem passe ativo.')).toBeVisible();
  await expect(page.getByText('Sem viagens recentes.')).toBeVisible();
  await expect(page.getByRole('button', { name: 'VER BILHETES DISPONÍVEIS' })).toBeVisible();
});

test('navega para meus bilhetes ao clicar em VER BILHETES DISPONÍVEIS', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app');
  await page.getByRole('button', { name: 'VER BILHETES DISPONÍVEIS' }).click();

  await expect(page).toHaveURL(/#\/app\/bilhetes\/meus$/);
  await expect(page.getByRole('heading', { name: 'Os meus bilhetes' })).toBeVisible();
});

test('navega para historico ao clicar em Ver Histórico', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app');
  await page.getByRole('button', { name: 'Ver Histórico' }).click();

  await expect(page).toHaveURL(/#\/app\/historico$/);
  await expect(page.getByRole('heading', { name: 'Histórico de Viagens' })).toBeVisible();
});

test('mostra viagem em curso e abre sheet de check-out', async ({ page }) => {
  const state = createApiState({
    activeTrip: {
      id: 999,
      estado: 'EM_CURSO',
      dataHoraEntrada: '2026-05-13T10:00:00',
      carreiraNum: '101',
      carreiraNome: 'Aeroporto',
      origemNome: 'Terminal',
    },
  });
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app');

  await expect(page.getByText('VIAGEM EM CURSO')).toBeVisible();
  await expect(page.locator('.viagem-ativa-carreira')).toHaveText('Aeroporto');
  await expect(page.locator('.viagem-ativa-origem')).toHaveText('desde Terminal');

  await page.getByRole('button', { name: 'FAZER CHECK-OUT' }).click();
  const dialog = page.locator('.q-dialog').last();
  await expect(dialog.locator('.sheet-title')).toHaveText('Fazer Check-out');
});

test('mostra badge de notificações no header', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app');

  await expect(page.locator('.q-badge').filter({ hasText: '2' })).toBeVisible();
});
