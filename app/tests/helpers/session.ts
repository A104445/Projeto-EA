import type { User } from '../../src/components/models';
import { defaultUser } from './data';
import type { Page } from '@playwright/test';

export function appUrl(path = '/'): string {
  if (path === '' || path === '/') {
    return '/';
  }

  if (path.startsWith('/#/')) {
    return path;
  }

  return `/#${path.startsWith('/') ? path : `/${path}`}`;
}

export async function gotoApp(page: Page, path = '/') {
  await page.goto(appUrl(path));
  await page.addStyleTag({
    content: `
      *, *::before, *::after {
        transition-duration: 0s !important;
        animation-duration: 0s !important;
        animation-delay: 0s !important;
      }
    `,
  });
}

export async function seedAuth(page: Page, user: User = defaultUser, token = 'test-token') {
  await page.addInitScript(
    ([authToken, authUser]) => {
      localStorage.setItem('et_token', authToken);
      localStorage.setItem('et_user', JSON.stringify(authUser));
    },
    [token, user] as const,
  );
}
