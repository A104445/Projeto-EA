import { expect, test } from '@playwright/test';
import { createApiState, mockApi } from './helpers/mockApi';
import { gotoApp, seedAuth } from './helpers/session';

test('lista o histórico e permite pesquisar por linha', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/historico');

  await expect(page.getByRole('heading', { name: 'Histórico de Viagens' })).toBeVisible();
  await expect(page.locator('.trip-carreira').filter({ hasText: 'Aeroporto' })).toBeVisible();
  await expect(page.locator('.trip-carreira').filter({ hasText: 'Universidade' })).toBeVisible();

  await page.getByPlaceholder('Pesquisar linha...').fill('202');
  await expect(page.locator('.trip-carreira').filter({ hasText: 'Universidade' })).toBeVisible();
  await expect(page.locator('.trip-carreira').filter({ hasText: 'Aeroporto' })).toHaveCount(0);
});

test('filtra viagens irregulares e limpa filtros', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/historico');
  await page.getByRole('button', { name: 'Filtrar' }).click();
  const dialog = page.locator('.q-dialog').last();
  await expect(dialog.getByText('Filtrar viagens')).toBeVisible();
  await dialog.locator('.status-chip').filter({ hasText: 'Irregulares' }).click();
  await dialog.getByRole('button', { name: 'Aplicar' }).click();

  await expect(page.locator('.badge-irregular')).toBeVisible();
  await expect(page.locator('.trip-carreira').filter({ hasText: 'Universidade' })).toBeVisible();
  await expect(page.locator('.trip-carreira').filter({ hasText: 'Aeroporto' })).toHaveCount(0);

  await page.getByText('Limpar tudo').click();
  await expect(page.locator('.trip-carreira').filter({ hasText: 'Aeroporto' })).toBeVisible();
  await expect(page.locator('.trip-carreira').filter({ hasText: 'Universidade' })).toBeVisible();
});

test('mostra estado vazio quando não há viagens', async ({ page }) => {
  const state = createApiState({ tripsHistory: [] });
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/historico');
  await expect(page.getByText('Ainda não tens viagens.')).toBeVisible();
});
