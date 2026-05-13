import { defineConfig, devices } from '@playwright/test';
export default defineConfig({
  testDir: './tests',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  reporter: 'html',
  timeout: 15_000,
  workers: process.env.CI ? 4 : 1,
  use: {
    baseURL: 'http://127.0.0.1:4174',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },

    /* Test against mobile viewports. */
    {
      name: 'Mobile Chrome',
      use: { ...devices['Pixel 7'] },
    },
    {
      name: 'Mobile Safari small viewport',
      use: { ...devices['iPhone SE'] },
    },
    {
      name: 'Mobile Safari',
      use: { ...devices['iPhone 15 Pro'] },
    },
  ],
  webServer: {
    command: 'npx quasar dev -p 4174 -H 127.0.0.1',
    url: 'http://127.0.0.1:4174',
    reuseExistingServer: false,
    env: {
      CI: '1',
      PLAYWRIGHT: '1',
    },
  },
});
