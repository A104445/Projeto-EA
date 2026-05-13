import { expect, test } from '@playwright/test';
import { createApiState, mockApi } from './helpers/mockApi';
import { gotoApp, seedAuth } from './helpers/session';

test('lista carreiras e permite pesquisar', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/percursos');

  await expect(page.getByRole('heading', { name: 'Percursos e Horários' })).toBeVisible();
  await expect(page.locator('.linha-card').filter({ hasText: 'Aeroporto' })).toBeVisible();
  await expect(page.locator('.linha-card').filter({ hasText: 'Universidade' })).toBeVisible();

  await page.getByPlaceholder('Procurar linha...').fill('202');
  await expect(page.locator('.linha-card').filter({ hasText: 'Universidade' })).toBeVisible();
  await expect(page.locator('.linha-card').filter({ hasText: 'Aeroporto' })).toHaveCount(0);
});

test('abre sheet de horários de uma carreira', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/percursos');

  await page
    .locator('.linha-card')
    .filter({ hasText: 'Aeroporto' })
    .getByRole('button')
    .click();

  const dialog = page.locator('.q-dialog').last();
  await expect(dialog.locator('.sheet-title')).toContainText('Horário — Aeroporto');
  // troca para segunda-feira para garantir que existem horários no mock
  await dialog.locator('.day-chip').filter({ hasText: 'seg.' }).click();
  await expect(dialog.getByText('Terminal')).toBeVisible();
  await expect(dialog.getByText('Campus')).toBeVisible();
  await expect(dialog.getByText('10:15')).toBeVisible();
});

test('mostra mensagem vazia para um dia sem horários', async ({ page }) => {
  const state = createApiState({ schedulesByRoute: { 201: [], 202: [] } });
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/percursos');

  await page
    .locator('.linha-card')
    .filter({ hasText: 'Aeroporto' })
    .getByRole('button')
    .click();

  const dialog = page.locator('.q-dialog').last();
  await expect(dialog.getByText('Sem horários para este dia.')).toBeVisible();
});
