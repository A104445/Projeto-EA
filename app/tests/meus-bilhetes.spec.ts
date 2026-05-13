import { expect, test } from '@playwright/test';
import { createApiState, mockApi } from './helpers/mockApi';
import { gotoApp, seedAuth } from './helpers/session';

test('mostra títulos disponíveis e utilizados em tabs separadas', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/bilhetes/meus');

  await expect(page.getByRole('heading', { name: 'Os meus bilhetes' })).toBeVisible();
  await expect(page.locator('.titulo-card-nome').filter({ hasText: 'Passe Anual' })).toBeVisible();
  await expect(page.locator('.titulo-card-nome').filter({ hasText: 'Pacote Viagens' })).toBeVisible();
  await expect(page.locator('.titulo-card-nome').filter({ hasText: 'Bilhete Individual' })).toHaveCount(0);

  await page.getByRole('button', { name: 'Utilizados' }).click();
  await expect(page.locator('.titulo-card-nome').filter({ hasText: 'Bilhete Individual' })).toBeVisible();
  await expect(page.locator('.titulo-card-nome').filter({ hasText: 'Passe Anual' })).toHaveCount(0);
});

test('mostra zonas e saldo nos títulos', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/bilhetes/meus');

  const pacoteCard = page.locator('.titulo-card').filter({ hasText: 'Pacote Viagens' });
  await expect(pacoteCard.locator('.zone-badge').filter({ hasText: 'Zona C' })).toBeVisible();
  await expect(pacoteCard.locator('.titulo-saldo')).toContainText('3 viagem(ns) restante(s)');
});

test('mostra mensagem vazia quando não há títulos disponíveis', async ({ page }) => {
  const state = createApiState({ titlesAll: [] });
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/bilhetes/meus');
  await expect(page.getByText('Nenhum título disponível.')).toBeVisible();

  await page.getByRole('button', { name: 'Utilizados' }).click();
  await expect(page.getByText('Nenhum título utilizado.')).toBeVisible();
});
