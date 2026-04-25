import { expect, test } from '@playwright/test';
import { createApiState, mockApi } from './helpers/mockApi';
import { gotoApp, seedAuth } from './helpers/session';

test('mostra dados do perfil e permite editar', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/perfil');

  await expect(page.getByRole('heading', { name: 'Perfil' })).toBeVisible();
  await expect(page.locator('.avatar-email')).toHaveText('joao@example.com');

  await page.getByRole('button', { name: 'Editar' }).click();
  await expect(page.getByText('Editar perfil')).toBeVisible();
  await page.getByPlaceholder('o teu nome').fill('Joao Pedro Silva');
  await page.getByPlaceholder('o teu número de telemóvel').fill('934567890');
  await page.locator('input[type="date"]').fill('2001-02-20');
  await page.getByRole('button', { name: 'GUARDAR' }).click();

  await expect(page.locator('.avatar-name')).toHaveText('Joao Pedro Silva');
  await expect(
    page.locator('.field-row').filter({ hasText: 'TELEMÓVEL' }).locator('.field-value'),
  ).toHaveText('934567890');
  await expect.poll(() => state.user.nome).toBe('Joao Pedro Silva');
});

test('mostra erro quando falha a gravação do perfil', async ({ page }) => {
  const state = createApiState({
    endpointErrors: {
      'PUT /api/users/me': {
        status: 500,
        body: { message: 'Internal error' },
      },
    },
  });
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/perfil');

  await page.getByRole('button', { name: 'Editar' }).click();
  await expect(page.getByText('Editar perfil')).toBeVisible();
  await page.getByPlaceholder('o teu nome').fill('Joao Falha');
  await page.getByRole('button', { name: 'GUARDAR' }).click();

  await expect(page.getByText('Não foi possível guardar as alterações. Tenta novamente...')).toBeVisible();
});
