import { expect, test } from '@playwright/test';
import { createApiState, mockApi } from './helpers/mockApi';
import { gotoApp, seedAuth } from './helpers/session';

test('lista notificações ativas', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/notificacoes');

  await expect(page.getByRole('heading', { name: 'Notificações' })).toBeVisible();
  await expect(page.getByText('Passe a expirar')).toBeVisible();
  await expect(page.getByText('O passe expira em breve.')).toBeVisible();
  await expect(page.getByText('Viagem irregular')).toBeVisible();
  await expect(page.getByText('A ultima viagem foi marcada como irregular.')).toBeVisible();
});

test('mostra mensagem vazia quando não há notificações', async ({ page }) => {
  const state = createApiState({ notifications: [] });
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/notificacoes');
  await expect(page.getByText('Sem notificações de momento.')).toBeVisible();
});

test('permite limpar todas as notificações', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app/notificacoes');
  await page.getByRole('button', { name: 'Limpar tudo' }).click();
  await expect(page.getByText('Sem notificações de momento.')).toBeVisible();
});

test('navega para notificações pelo sino do header', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app');
  await page.locator('.home-header-top').getByRole('button').click();

  await expect(page).toHaveURL(/#\/app\/notificacoes$/);
});
