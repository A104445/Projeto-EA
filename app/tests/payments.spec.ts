import { expect, test } from '@playwright/test';
import { createApiState, mockApi } from './helpers/mockApi';
import { gotoApp, seedAuth } from './helpers/session';

test('mostra opções de compra na bilheteira', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/bilhetes');

  await expect(page.getByRole('heading', { name: 'Bilheteira' })).toBeVisible();
  await expect(page.getByText('Passe Anual')).toBeVisible();
  await expect(page.getByText('Pacote de Viagens')).toBeVisible();
  await expect(page.getByText('Bilhete Individual')).toBeVisible();
});

test('compra um passe anual', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/bilhetes');
  await page.locator('.opcao-card').filter({ hasText: 'Passe Anual' }).click();

  await expect(page).toHaveURL(/#\/app\/bilhetes\/comprar\/passe$/);
  await page.locator('.zona-chip').filter({ hasText: 'Zona A' }).click();
  await page.locator('.zona-chip').filter({ hasText: 'Zona B' }).click();
  await expect(page.locator('.total-chip')).toContainText('30,00€');

  await page.getByRole('button', { name: 'Avançar para Compra' }).click();
  await expect(page).toHaveURL(/#\/app\/bilhetes\/checkout$/);

  await expect(page.locator('.resumo-main')).toHaveText('Passe Anual');
  await page.locator('.payment-option').filter({ hasText: 'Cartão de Débito ou Crédito' }).click();
  await page.getByRole('button', { name: 'Finalizar Compra' }).click();

  await expect(page.getByText('Compra concluída!')).toBeVisible();
  await expect(page.getByText('O teu título foi adicionado à conta.')).toBeVisible();
  await expect.poll(() => state.titlesAll.length).toBeGreaterThan(3);
});

test('compra um bilhete individual', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/bilhetes');
  await page.locator('.opcao-card').filter({ hasText: 'Bilhete Individual' }).click();

  await expect(page).toHaveURL(/#\/app\/bilhetes\/comprar\/bilhete$/);
  await page.locator('.carreira-item').filter({ hasText: 'Aeroporto' }).click();
  await expect(page.locator('.total-chip')).toContainText('1,20€');

  await page.getByRole('button', { name: 'Avançar para Compra' }).click();
  await expect(page.locator('.resumo-main')).toContainText('Aeroporto');
  await page.locator('.payment-option').filter({ hasText: 'Cartão de Débito ou Crédito' }).click();
  await page.getByRole('button', { name: 'Finalizar Compra' }).click();

  await expect(page.getByText('Compra concluída!')).toBeVisible();
  await expect.poll(() => state.titlesAll.length).toBeGreaterThan(3);
});

test('compra um pacote de viagens', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/bilhetes');
  await page.locator('.opcao-card').filter({ hasText: 'Pacote de Viagens' }).click();

  await expect(page).toHaveURL(/#\/app\/bilhetes\/comprar\/pacote$/);
  const zonaC = page.locator('.zona-chip').filter({ hasText: 'Zona C' });
  await zonaC.scrollIntoViewIfNeeded();
  await zonaC.evaluate((el) => el.scrollIntoView({ block: 'center' }));
  await zonaC.click();
  await expect(page.locator('.num-viagens-val')).toHaveText('5');
  await expect(page.locator('.total-chip')).toContainText('4,50€');

  await page.getByRole('button', { name: 'Avançar para Compra' }).click();
  await expect(page.locator('.resumo-main')).toContainText('Pacote');
  await page.locator('.payment-option').filter({ hasText: 'Google Pay' }).click();
  await page.getByRole('button', { name: 'Finalizar Compra' }).click();

  await expect(page.getByText('Compra concluída!')).toBeVisible();
  await expect.poll(() => state.titlesAll.length).toBeGreaterThan(3);
});

test('pesquisa carreira na compra de bilhete individual', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/bilhetes/comprar/bilhete');
  await expect(page.locator('.carreira-item').filter({ hasText: 'Aeroporto' })).toBeVisible();
  await expect(page.locator('.carreira-item').filter({ hasText: 'Universidade' })).toBeVisible();

  await page.getByPlaceholder('Pesquisar carreira...').fill('Univ');
  await expect(page.locator('.carreira-item').filter({ hasText: 'Universidade' })).toBeVisible();
  await expect(page.locator('.carreira-item').filter({ hasText: 'Aeroporto' })).toHaveCount(0);
});

test('botão de avançar fica desativado sem seleção', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/bilhetes/comprar/passe');
  await expect(page.getByRole('button', { name: 'Avançar para Compra' })).toBeDisabled();
  await expect(page.locator('.total-chip')).toContainText('—');
});
