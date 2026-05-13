import { expect, test } from '@playwright/test';
import { createApiState, mockApi } from './helpers/mockApi';
import { gotoApp, seedAuth } from './helpers/session';

test('mostra dados do perfil', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/perfil');

  await expect(page.locator('.user-name')).toHaveText('Joao Silva');
  await expect(page.locator('.user-meta')).toContainText('joao@example.com');
  await expect(page.locator('.user-meta')).toContainText('912345678');
  await expect(page.locator('.avatar-initials')).toHaveText('JS');
  await expect(page.getByText('Alterar Informações Pessoais')).toBeVisible();
});

test('navega para editar perfil e guarda alterações', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/perfil');
  await page.getByText('Alterar Informações Pessoais').click();

  await expect(page).toHaveURL(/#\/app\/perfil\/editar$/);
  await expect(page.getByRole('heading', { name: 'Perfil' })).toBeVisible();

  await page.locator('.form-input').filter({ hasText: 'Nome' }).locator('input').fill('Joao Pedro Silva');
  await page.locator('.form-input').filter({ hasText: 'Telefone' }).locator('input').fill('934567890');
  await page.getByRole('button', { name: 'Guardar alterações' }).click();

  await expect(page).toHaveURL(/#\/app\/perfil$/);
  await expect(page.locator('.user-name')).toHaveText('Joao Pedro Silva');
  await expect(page.locator('.user-meta')).toContainText('934567890');
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

  await gotoApp(page, '/app/perfil/editar');

  await page.locator('.form-input').filter({ hasText: 'Nome' }).locator('input').fill('Joao Falha');
  await page.getByRole('button', { name: 'Guardar alterações' }).click();

  await expect(
    page.getByText('Não foi possível guardar as alterações. Tenta novamente...'),
  ).toBeVisible();
});

test('campo de e-mail é apenas-leitura', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/perfil/editar');

  const emailInput = page.locator('.form-input').filter({ hasText: 'E-mail' }).locator('input');
  await expect(emailInput).toHaveAttribute('readonly', '');
  await expect(emailInput).toHaveValue('joao@example.com');
});
