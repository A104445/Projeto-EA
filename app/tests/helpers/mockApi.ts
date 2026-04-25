import type { Page, Route } from '@playwright/test';
import type {
  CarreiraDTO,
  HorarioDTO,
  NotificacaoDTO,
  TituloDTO,
  User,
  ViagemDTO,
  ZonaDTO,
} from '../../src/components/models';
import {
  defaultActiveTitles,
  defaultAllTitles,
  defaultHistoryTrips,
  defaultNotifications,
  defaultRoutes,
  defaultSchedulesByRoute,
  defaultUser,
  defaultZones,
} from './data';

type ApiError = {
  status: number;
  body?: unknown;
};

export type ApiState = {
  token: string;
  user: User;
  titlesAll: TituloDTO[];
  titlesActive: TituloDTO[];
  zones: ZonaDTO[];
  routes: CarreiraDTO[];
  schedulesByRoute: Record<number, HorarioDTO[]>;
  tripsHistory: ViagemDTO[];
  activeTrip: ViagemDTO | null;
  notifications: NotificacaoDTO[];
  loginError: ApiError | undefined;
  registerError: ApiError | undefined;
  endpointErrors: Record<string, ApiError>;
};

function clone<T>(value: T): T {
  return JSON.parse(JSON.stringify(value)) as T;
}

export function createApiState(overrides: Partial<ApiState> = {}): ApiState {
  return {
    token: overrides.token ?? 'test-token',
    user: clone(overrides.user ?? defaultUser),
    titlesAll: clone(overrides.titlesAll ?? defaultAllTitles),
    titlesActive: clone(overrides.titlesActive ?? defaultActiveTitles),
    zones: clone(overrides.zones ?? defaultZones),
    routes: clone(overrides.routes ?? defaultRoutes),
    schedulesByRoute: clone(overrides.schedulesByRoute ?? defaultSchedulesByRoute),
    tripsHistory: clone(overrides.tripsHistory ?? defaultHistoryTrips),
    activeTrip: clone(overrides.activeTrip ?? null),
    notifications: clone(overrides.notifications ?? defaultNotifications),
    loginError: overrides.loginError,
    registerError: overrides.registerError,
    endpointErrors: { ...(overrides.endpointErrors ?? {}) },
  };
}

async function fulfillJson(route: Route, status: number, body: unknown) {
  await route.fulfill({
    status,
    contentType: 'application/json',
    body: JSON.stringify(body),
  });
}

function endpointKey(method: string, pathname: string): string {
  return `${method.toUpperCase()} ${pathname}`;
}

function nextTitleId(state: ApiState): number {
  return Math.max(0, ...state.titlesAll.map((title) => title.ID)) + 1;
}

function zoneNamesFromIds(state: ApiState, zonaIDs: number[]): string[] {
  return state.zones.filter((zona) => zonaIDs.includes(zona.ID)).map((zona) => zona.nome);
}

export async function mockApi(page: Page, state: ApiState) {
  await page.route('**/api/**', async (route) => {
    const request = route.request();
    const method = request.method().toUpperCase();
    const url = new URL(request.url());
    const pathname = url.pathname;
    const key = endpointKey(method, pathname);

    const forcedError = state.endpointErrors[key];
    if (forcedError) {
      await fulfillJson(
        route,
        forcedError.status,
        forcedError.body ?? { message: `Forced error for ${key}` },
      );
      return;
    }

    if (key === 'POST /api/auth/login') {
      if (state.loginError) {
        await fulfillJson(
          route,
          state.loginError.status,
          state.loginError.body ?? { error: 'Login failed' },
        );
        return;
      }
      await fulfillJson(route, 200, { token: state.token, user: state.user });
      return;
    }

    if (key === 'POST /api/auth/register') {
      if (state.registerError) {
        await fulfillJson(
          route,
          state.registerError.status,
          state.registerError.body ?? { error: 'Register failed' },
        );
        return;
      }

      const payload = request.postDataJSON() as { nome: string; email: string };
      state.user = {
        ...state.user,
        nome: payload.nome,
        email: payload.email,
      };
      await fulfillJson(route, 200, { token: state.token, user: state.user });
      return;
    }

    if (key === 'GET /api/tickets/mine') {
      await fulfillJson(route, 200, state.titlesAll);
      return;
    }

    if (key === 'GET /api/tickets/mine/active') {
      await fulfillJson(route, 200, state.titlesActive);
      return;
    }

    if (key === 'POST /api/tickets/annual-pass') {
      const payload = request.postDataJSON() as { zonaIDs: number[] };
      const title: TituloDTO = {
        ID: nextTitleId(state),
        tipo: 'PASSE_ANUAL',
        estado: 'ATIVO',
        valido: true,
        zonas: zoneNamesFromIds(state, payload.zonaIDs),
        dataFim: '2027-12-31',
      };
      state.titlesAll.unshift(title);
      state.titlesActive.unshift(title);
      await fulfillJson(route, 200, title);
      return;
    }

    if (key === 'POST /api/tickets/single-ticket') {
      const payload = request.postDataJSON() as { carreiraID: number };
      const routeInfo = state.routes.find((entry) => entry.ID === payload.carreiraID);
      const title: TituloDTO = {
        ID: nextTitleId(state),
        tipo: 'BILHETE_INDIVIDUAL',
        estado: 'ATIVO',
        valido: true,
        zonas: routeInfo ? ['Zona A'] : [],
      };
      state.titlesAll.unshift(title);
      state.titlesActive.unshift(title);
      await fulfillJson(route, 200, title);
      return;
    }

    if (key === 'POST /api/tickets/trip-pack') {
      const payload = request.postDataJSON() as { numViagens: number; zonaIDs: number[] };
      const title: TituloDTO = {
        ID: nextTitleId(state),
        tipo: 'PACOTE_VIAGENS',
        estado: 'ATIVO',
        valido: true,
        zonas: zoneNamesFromIds(state, payload.zonaIDs),
        saldoRestante: payload.numViagens,
      };
      state.titlesAll.unshift(title);
      state.titlesActive.unshift(title);
      await fulfillJson(route, 200, title);
      return;
    }

    if (key === 'GET /api/zones') {
      await fulfillJson(route, 200, state.zones);
      return;
    }

    if (key === 'GET /api/routes') {
      await fulfillJson(route, 200, state.routes);
      return;
    }

    if (method === 'GET' && pathname.match(/^\/api\/routes\/\d+\/schedules$/)) {
      const routeId = Number(pathname.split('/')[3]);
      await fulfillJson(route, 200, state.schedulesByRoute[routeId] ?? []);
      return;
    }

    if (key === 'GET /api/trips/history') {
      await fulfillJson(route, 200, state.tripsHistory);
      return;
    }

    if (key === 'GET /api/trips/active') {
      await fulfillJson(route, 200, state.activeTrip);
      return;
    }

    if (key === 'GET /api/notifications') {
      await fulfillJson(route, 200, state.notifications);
      return;
    }

    if (key === 'PUT /api/users/me') {
      const payload = request.postDataJSON() as Partial<User>;
      state.user = {
        ...state.user,
        ...payload,
      };
      await fulfillJson(route, 200, state.user);
      return;
    }

    await fulfillJson(route, 500, { message: `Unhandled API route: ${key}` });
  });
}
