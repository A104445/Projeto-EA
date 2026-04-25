import { expect, test } from '@playwright/test';
import { createApiState, mockApi } from './helpers/mockApi';
import { gotoApp } from './helpers/session';

test('faz login com sucesso e entra na app', async ({ page }) => {
  const state = createApiState();
  await mockApi(page, state);

  await gotoApp(page, '/login');
  await expect(page.getByRole('heading', { name: 'Iniciar Sessão' })).toBeVisible();
  await page.locator('input[type="email"]').fill('joao@example.com');
  await page.locator('input[type="password"]').fill('segredo123');
  await page.getByRole('button', { name: 'INICIAR SESSÃO' }).click();

  await expect(page).toHaveURL(/#\/app$/);
  await expect(page.getByText('Olá,')).toBeVisible();
  await expect
    .poll(async () => page.evaluate(() => localStorage.getItem('et_token')))
    .toBe(state.token);
});

test('mostra erro de login quando a API devolve 401', async ({ page }) => {
  const state = createApiState({
    loginError: {
      status: 401,
      body: { error: 'Credenciais inválidas' },
    },
  });
  await mockApi(page, state);

  await gotoApp(page, '/login');
  await expect(page.getByRole('heading', { name: 'Iniciar Sessão' })).toBeVisible();
  await page.locator('input[type="email"]').fill('joao@example.com');
  await page.locator('input[type="password"]').fill('errada');
  await page.getByRole('button', { name: 'INICIAR SESSÃO' }).click();

  await expect(page.getByText('E-mail ou palavra-passe incorretos!')).toBeVisible();
  await expect(page).toHaveURL(/#\/login$/);
});

test('regista utilizador com sucesso', async ({ page }) => {
  const state = createApiState();
  await mockApi(page, state);

  await gotoApp(page, '/register');
  await expect(page.getByRole('heading', { name: 'Registar' })).toBeVisible();
  await page.locator('input').nth(0).fill('Maria Costa');
  await page.locator('input[type="email"]').fill('maria@example.com');
  await page.locator('input[type="password"]').fill('segredo123');
  await page.getByRole('button', { name: 'CRIAR CONTA' }).click();

  await expect(page).toHaveURL(/#\/app$/);
  await expect(page.getByText('Maria!')).toBeVisible();
});

test('mostra erro de e-mail duplicado no registo', async ({ page }) => {
  const state = createApiState({
    registerError: {
      status: 400,
      body: { error: 'Email já registado!' },
    },
  });
  await mockApi(page, state);

  await gotoApp(page, '/register');
  await expect(page.getByRole('heading', { name: 'Registar' })).toBeVisible();
  await page.locator('input').nth(0).fill('Maria Costa');
  await page.locator('input[type="email"]').fill('maria@example.com');
  await page.locator('input[type="password"]').fill('segredo123');
  await page.getByRole('button', { name: 'CRIAR CONTA' }).click();

  await expect(page.getByText('Já existe uma conta com este e-mail!')).toBeVisible();
  await expect(page).toHaveURL(/#\/register$/);
});
