import { expect, test } from '@playwright/test';
import { createApiState, mockApi } from './helpers/mockApi';
import { gotoApp, seedAuth } from './helpers/session';

test('faz check-in numa paragem usando código manual', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app');

  await page.getByText('Fazer Check-in').click();
  const dialog = page.locator('.q-dialog').last();
  await expect(dialog.locator('.sheet-title')).toHaveText('Fazer Check-in');

  await dialog.locator('.qr-text-input').fill('QR-TERMINAL');
  await dialog.locator('.qr-text-input').press('Enter');

  // passo 2: confirmar paragem + selecionar carreira/título
  await expect(dialog.getByText('Terminal')).toBeVisible();
  await expect(dialog.locator('.field-label').filter({ hasText: 'CARREIRA' })).toBeVisible();
  await dialog.locator('.select-list').first().locator('.q-item').first().click();
  await dialog.locator('.select-list').last().locator('.q-item').first().click();

  await dialog.getByRole('button', { name: 'INICIAR VIAGEM' }).click();

  // passo 3: sucesso
  await expect(dialog.getByText('Check-in realizado com sucesso!')).toBeVisible();
  await expect.poll(() => state.activeTrip?.estado).toBe('EM_CURSO');
});

test('mostra erro de paragem desconhecida no check-in', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app');
  await page.getByText('Fazer Check-in').click();
  const dialog = page.locator('.q-dialog').last();

  await dialog.locator('.qr-text-input').fill('QR-INVALIDO');
  await dialog.locator('.qr-text-input').press('Enter');

  await expect(dialog.getByText('Paragem não reconhecida:')).toBeVisible();
});

test('faz check-out de viagem em curso', async ({ page }) => {
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
  await page.getByRole('button', { name: 'FAZER CHECK-OUT' }).click();
  const dialog = page.locator('.q-dialog').last();

  await dialog.locator('.qr-text-input').fill('QR-CAMPUS');
  await dialog.locator('.qr-text-input').press('Enter');

  await expect(dialog.getByText('Campus')).toBeVisible();
  await dialog.getByRole('button', { name: 'CONFIRMAR SAÍDA' }).click();

  await expect(page.getByText('Viagem concluída!')).toBeVisible();
  await expect.poll(() => state.activeTrip).toBeNull();
});
