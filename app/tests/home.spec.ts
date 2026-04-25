import { expect, test } from '@playwright/test';
import { createApiState, mockApi } from './helpers/mockApi';
import { gotoApp, seedAuth } from './helpers/session';

test('mostra greeting, passe ativo e viagens recentes', async ({ page }) => {
  const state = createApiState();
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app');

  await expect(page.getByText('Olá,')).toBeVisible();
  await expect(page.getByText('Joao!')).toBeVisible();
  await expect(page.getByText('PASSE ATIVO')).toBeVisible();
  await expect(page.getByText('Zona A')).toBeVisible();
  await expect(page.getByText('Zona B')).toBeVisible();
  await expect(page.getByText('Viagens recentes')).toBeVisible();
  await expect(page.getByText('101 - Aeroporto')).toBeVisible();
  await expect(page.getByText('202 - Universidade')).toBeVisible();
});

test('mostra estados vazios quando não há títulos nem viagens', async ({ page }) => {
  const state = createApiState({
    titlesActive: [],
    tripsHistory: [],
    notifications: [],
  });
  await seedAuth(page, state.user, state.token);
  await mockApi(page, state);

  await gotoApp(page, '/app');

  await expect(page.getByText('Sem passe ativo')).toBeVisible();
  await expect(page.getByText('Sem viagens recentes')).toBeVisible();
  await expect(page.getByRole('button', { name: 'VER BILHETES DISPONÍVEIS' })).toBeVisible();
});
