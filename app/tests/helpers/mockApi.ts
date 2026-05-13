import type { Page, Route } from '@playwright/test';
import type {
  CarreiraDTO,
  HorarioDTO,
  NotificacaoDTO,
  ParagemDTO,
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
  defaultRoutesByStop,
  defaultSchedulesByRoute,
  defaultStops,
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
  stops: ParagemDTO[];
  routesByStop: Record<number, CarreiraDTO[]>;
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
    stops: clone(overrides.stops ?? defaultStops),
    routesByStop: clone(overrides.routesByStop ?? defaultRoutesByStop),
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
  return Math.max(0, ...state.titlesAll.map((title) => title.id)) + 1;
}

function nextTripId(state: ApiState): number {
  const ids = state.tripsHistory.map((t) => t.id);
  if (state.activeTrip) ids.push(state.activeTrip.id);
  return Math.max(0, ...ids) + 1;
}

function zoneNamesFromIds(state: ApiState, zonaIDs: number[]): string[] {
  return state.zones.filter((zona) => zonaIDs.includes(zona.id)).map((zona) => zona.nome);
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

    if (key === 'POST /api/auth/google') {
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
        id: nextTitleId(state),
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
      const routeInfo = state.routes.find((entry) => entry.id === payload.carreiraID);
      const title: TituloDTO = {
        id: nextTitleId(state),
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
        id: nextTitleId(state),
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

    if (key === 'GET /api/stops/by-qr') {
      const qr = url.searchParams.get('qr') ?? '';
      const stop = state.stops.find((s) => s.qr === qr);
      if (!stop) {
        await fulfillJson(route, 404, { message: 'Paragem não encontrada' });
        return;
      }
      await fulfillJson(route, 200, stop);
      return;
    }

    if (method === 'GET' && pathname.match(/^\/api\/stops\/\d+\/routes$/)) {
      const stopId = Number(pathname.split('/')[3]);
      await fulfillJson(route, 200, state.routesByStop[stopId] ?? []);
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

    if (key === 'POST /api/trips/check-in') {
      const payload = request.postDataJSON() as {
        paragemID: number;
        carreiraID: number;
        tituloID: number;
      };
      const stop = state.stops.find((s) => s.id === payload.paragemID);
      const carreira = state.routes.find((c) => c.id === payload.carreiraID);
      const trip: ViagemDTO = {
        id: nextTripId(state),
        estado: 'EM_CURSO',
        dataHoraEntrada: new Date().toISOString(),
        carreiraNum: carreira?.num ?? '',
        carreiraNome: carreira?.nome ?? '',
        origemNome: stop?.nome ?? '',
      };
      state.activeTrip = trip;
      await fulfillJson(route, 200, trip);
      return;
    }

    if (method === 'POST' && pathname.match(/^\/api\/trips\/\d+\/check-out$/)) {
      const viagemID = Number(pathname.split('/')[3]);
      const payload = request.postDataJSON() as { paragemID: number };
      const stop = state.stops.find((s) => s.id === payload.paragemID);
      const finished: ViagemDTO = {
        ...(state.activeTrip ?? ({} as ViagemDTO)),
        id: viagemID,
        estado: 'CONCLUIDA',
        dataHoraSaida: new Date().toISOString(),
        destinoNome: stop?.nome ?? '',
        irregular: false,
      };
      state.activeTrip = null;
      state.tripsHistory.unshift(finished);
      await fulfillJson(route, 200, finished);
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
