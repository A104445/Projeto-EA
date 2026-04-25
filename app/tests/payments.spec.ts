import { expect, test } from '@playwright/test';
import { createApiState, mockApi } from './helpers/mockApi';
import { gotoApp, seedAuth } from './helpers/session';

test('mostra títulos ativos do utilizador', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/bilhetes');

  await expect(page.getByRole('heading', { name: 'Bilhetes' })).toBeVisible();
  await expect(page.locator('.titulo-tipo').filter({ hasText: 'PASSE ANUAL' })).toBeVisible();
  await expect(page.locator('.titulo-tipo').filter({ hasText: 'PACOTE VIAGENS' })).toBeVisible();
});

test('compra um passe anual', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/bilhetes');
  await page.locator('.compra-card').filter({ hasText: 'Passe Anual' }).click();
  const dialog = page.locator('.q-dialog').last();
  await expect(dialog.locator('.sheet-title')).toHaveText('Passe Anual');
  await dialog.getByRole('button', { name: 'Zona A' }).click();
  await dialog.getByRole('button', { name: 'Zona B' }).click();
  await dialog.getByRole('button', { name: 'Confirmar compra' }).click();

  await expect(page.getByText('Compra concluída!')).toBeVisible();
  await expect(page.getByText('O teu título foi adicionado à conta.')).toBeVisible();
  await expect.poll(() => state.titlesAll.length).toBe(3);
});

test('compra um bilhete individual', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/bilhetes');
  await page.locator('.compra-card').filter({ hasText: 'Bilhete Individual' }).click();
  const dialog = page.locator('.q-dialog').last();
  await expect(dialog.locator('.sheet-title')).toHaveText('Bilhete Individual');
  await dialog.getByText('101 — Aeroporto').click();
  await dialog.getByRole('button', { name: 'Confirmar compra' }).click();

  await expect(page.getByText('Compra concluída!')).toBeVisible();
  await expect.poll(() => state.titlesAll.length).toBe(3);
});

test('compra um pacote de viagens', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/bilhetes');
  await page.locator('.compra-card').filter({ hasText: 'Pacote de Viagens' }).click();
  const dialog = page.locator('.q-dialog').last();
  await expect(dialog.locator('.sheet-title')).toHaveText('Pacote de Viagens');
  await dialog.getByRole('button', { name: 'Zona C' }).click();
  await dialog.getByRole('button', { name: 'Confirmar compra' }).click();

  await expect(page.getByText('Compra concluída!')).toBeVisible();
  await expect.poll(() => state.titlesAll.length).toBe(3);
});
