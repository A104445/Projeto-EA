"""
cenários:
  1. ConsultaHorariosUser  (peso 3) — login + listar carreiras + horários
  2. ViagemCompletaUser    (peso 3) — login + check-in + check-out
  3. ComprarTituloUser     (peso 1) — login + compra de bilhete + ver títulos

patamares: 100, 200, 500, 1000, 1500, 3000 utilizadores

pré-requisitos:
  pip install locust
  python data/users.py [N]         # gera N utilizadores de carga
  # carregar users.sql na BD
  docker compose -f docker-compose.loadtest.yml up --scale backend=1 -d
  # (repetir com --scale backend=2 e --scale backend=3)

uso:
  locust -f load-tests/locustfile.py --host http://localhost
  # abrir http://localhost:8089
"""

import random
import threading

from locust import HttpUser, between, task

PASSWORD = "1234567890"
NUM_LOAD_USERS = 3000

_lock = threading.Lock()
_counter = 0


def _next_user_num() -> int:
    global _counter
    with _lock:
        num = _counter % NUM_LOAD_USERS + 1
        _counter += 1
    return num


# ---------------------------------------------------------------------------
# cenário 1: Consulta de horários (peso 3)
# fluxo: login → listar carreiras → horários de carreira aleatória
# ---------------------------------------------------------------------------

class ConsultaHorariosUser(HttpUser):
    weight = 3
    wait_time = between(1, 3)

    def on_start(self):
        num = _next_user_num()
        res = self.client.post(
            "/api/auth/login",
            json={"email": f"load_{num:03d}@test.pt", "password": PASSWORD},
            name="/api/auth/login [setup]",
        )
        self.headers = {}
        self.route_ids = [1]
        if res.status_code != 200:
            return
        self.headers = {"Authorization": f"Bearer {res.json()['token']}"}

        routes = self.client.get(
            "/api/routes", headers=self.headers, name="/api/routes [setup]"
        )
        self.route_ids = (
            [r["id"] for r in routes.json()] if routes.status_code == 200 else [1]
        )

    @task(1)
    def listar_carreiras(self):
        self.client.get("/api/routes", headers=self.headers, name="/api/routes")

    @task(2)
    def consultar_horarios(self):
        rid = random.choice(self.route_ids)
        self.client.get(
            f"/api/routes/{rid}/schedules",
            headers=self.headers,
            name="/api/routes/{id}/schedules",
        )


# ---------------------------------------------------------------------------
# cenário 2: Viagem completa (peso 3)
# fluxo: login → check-in → check-out
# ---------------------------------------------------------------------------

class ViagemCompletaUser(HttpUser):
    weight = 3
    wait_time = between(2, 5)

    def on_start(self):
        num = _next_user_num()
        res = self.client.post(
            "/api/auth/login",
            json={"email": f"load_{num:03d}@test.pt", "password": PASSWORD},
            name="/api/auth/login [setup]",
        )
        self.headers = {}
        self.titulo_id = None
        self.route_info = None
        if res.status_code != 200:
            return
        token = res.json()["token"]
        self.headers = {"Authorization": f"Bearer {token}"}

        # título de transporte ativo
        titulos = self.client.get(
            "/api/tickets/mine/active",
            headers=self.headers,
            name="/api/tickets/mine/active [setup]",
        )
        data = titulos.json() if titulos.status_code == 200 else []
        self.titulo_id = data[0]["id"] if data else None

        # carreira com pelo menos 2 paragens distintas
        self.route_info = None
        routes = self.client.get(
            "/api/routes", headers=self.headers, name="/api/routes [setup]"
        )
        if routes.status_code == 200:
            sample = random.sample(routes.json(), min(5, len(routes.json())))
            for route in sample:
                sch = self.client.get(
                    f"/api/routes/{route['id']}/schedules",
                    headers=self.headers,
                    name="/api/routes/{id}/schedules [setup]",
                )
                if sch.status_code == 200:
                    stop_ids = list({h["paragemId"] for h in sch.json()})
                    if len(stop_ids) >= 2:
                        self.route_info = {
                            "carreiraID": route["id"],
                            "paragemIn": stop_ids[0],
                            "paragemOut": stop_ids[-1],
                        }
                        break

    @task
    def fazer_viagem(self):
        if not self.titulo_id or not self.route_info:
            return

        # se ficou viagem ativa de iteração anterior, faz check-out primeiro.
        active = self.client.get(
            "/api/trips/active", headers=self.headers, name="/api/trips/active"
        )
        if active.status_code == 200 and active.content:
            trip = active.json()
            if trip and trip.get("id"):
                self.client.post(
                    f"/api/trips/{trip['id']}/check-out",
                    json={"paragemID": self.route_info["paragemOut"]},
                    headers=self.headers,
                    name="/api/trips/{id}/check-out",
                )
                return

        # check-in
        ci = self.client.post(
            "/api/trips/check-in",
            json={
                "paragemID": self.route_info["paragemIn"],
                "carreiraID": self.route_info["carreiraID"],
                "tituloID": self.titulo_id,
            },
            headers=self.headers,
            name="/api/trips/check-in",
        )
        if ci.status_code != 200:
            return

        # check-out
        self.client.post(
            f"/api/trips/{ci.json().get('id')}/check-out",
            json={"paragemID": self.route_info["paragemOut"]},
            headers=self.headers,
            name="/api/trips/{id}/check-out",
        )


# ---------------------------------------------------------------------------
# cenário 3: Compra de título (peso 1)
# fluxo: login → compra bilhete individual → consulta os seus títulos
# ---------------------------------------------------------------------------

class ComprarTituloUser(HttpUser):
    weight = 1
    wait_time = between(5, 10)

    def on_start(self):
        num = _next_user_num()
        res = self.client.post(
            "/api/auth/login",
            json={"email": f"load_{num:03d}@test.pt", "password": PASSWORD},
            name="/api/auth/login [setup]",
        )
        self.headers = {}
        self.carreira_id = 1
        if res.status_code != 200:
            return
        token = res.json()["token"]
        self.headers = {"Authorization": f"Bearer {token}"}

        routes = self.client.get(
            "/api/routes", headers=self.headers, name="/api/routes [setup]"
        )
        carreiras = routes.json() if routes.status_code == 200 else []
        self.carreira_id = carreiras[0]["id"] if carreiras else 1

    @task(1)
    def comprar_bilhete(self):
        self.client.post(
            "/api/tickets/single-ticket",
            json={"carreiraID": self.carreira_id},
            headers=self.headers,
            name="/api/tickets/single-ticket",
        )

    @task(2)
    def ver_titulos(self):
        self.client.get(
            "/api/tickets/mine", headers=self.headers, name="/api/tickets/mine"
        )
