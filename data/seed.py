"""
seed.py
gera seed.sql a partir do dataset UrbanBus para o projecto EasyTrip/AirBus.

uso:
    python data/seed.py

ficheiros necessários em data/urbanbus/:
    - BusStopList.csv
    - BusRoutes.pickle
    - BUS_DATA_*_trunc.csv   (gerado por truncate.py)
"""

import os, pickle
import bcrypt
import pandas as pd

MAX_STOPS        = 800      # paragens a incluir
MAX_ROUTES       = 200      # carreiras a incluir
BUS_PER_ROUTE = 3
ZONE_SAMPLE_ROWS = 1_000_000  # linhas da _trunc utilizadas para calcular frequências
BATCH_SIZE       = 1_000    # registos por INSERT (horários)

BASE = os.path.join(os.path.dirname(__file__), "urbanbus")
OUT  = os.path.join(os.path.dirname(__file__), "seed.sql")

def esc(s) -> str:
    return str(s).replace("'", "''")

def clean_stop(name: str) -> str:
    return name.replace("_", "-")

def clean_route(num: str) -> str:
    return num.removeprefix("SER_")

print("A carregar BusStopList.csv...")
stops_df = pd.read_csv(os.path.join(BASE, "BusStopList.csv"))
stops_df.columns = ["fid", "stop_code"]
stops_df["stop_code"] = stops_df["stop_code"].astype(str).str.strip()
stops_df = stops_df.drop_duplicates("stop_code").reset_index(drop=True)
print(f"  {len(stops_df)} paragens únicas")

print("A carregar BusRoutes.pickle...")
with open(os.path.join(BASE, "BusRoutes.pickle"), "rb") as f:
    bus_routes: dict = pickle.load(f)
print(f"  {len(bus_routes)} carreiras")

routes_list = []
for svc, df in bus_routes.items():
    if isinstance(df, pd.DataFrame):
        col = next((c for c in df.columns if "stop" in c.lower()), df.columns[0])
        stops_seq = df[col].astype(str).str.strip().tolist()
    else:
        stops_seq = []
    routes_list.append({"num": clean_route(str(svc)), "stops": stops_seq})

print(f"A calcular frequências das paragens (amostra {ZONE_SAMPLE_ROWS:,} linhas)...")
trunc_candidates = sorted([
    f for f in os.listdir(BASE)
    if f.startswith("BUS_DATA") and f.endswith("_trunc.csv")
])
if trunc_candidates:
    txn_path   = os.path.join(BASE, trunc_candidates[0])
    txn_sample = pd.read_csv(txn_path, nrows=ZONE_SAMPLE_ROWS,
                             usecols=["Boarding_stop_stn", "Alighting_stop_stn"])
    freq = pd.concat([txn_sample["Boarding_stop_stn"],
                      txn_sample["Alighting_stop_stn"]]).value_counts()
    freq.index = freq.index.astype(str)
    print(f"  {len(freq)} paragens com frequência calculada.")
else:
    freq = pd.Series(dtype=int)
    print("  Sem ficheiro _trunc — zonas atribuídas ciclicamente.")

def zona_id(stop_code: str, idx: int) -> int:
    """1=Centro, 2=Suburbana, 3=Periférica"""
    if stop_code in freq.index:
        pct = freq.rank(pct=True)[stop_code]
        if pct >= 0.67: return 1
        if pct >= 0.33: return 2
        return 3
    return (idx % 3) + 1

stops_df    = stops_df.head(MAX_STOPS).reset_index(drop=True)
routes_list = routes_list[:MAX_ROUTES]

stop_pk = {row["stop_code"]: i + 1 for i, row in stops_df.iterrows()}

print("A gerar hash BCrypt para '1234567890'...")
PWD = bcrypt.hashpw(b"1234567890", bcrypt.gensalt(10)).decode()
test_users = [
    ("Ana Silva",       "ana@easytrip.pt"),
    ("Bruno Costa",     "bruno@easytrip.pt"),
    ("Carla Mendes",    "carla@easytrip.pt"),
    ("David Ferreira",  "david@easytrip.pt"),
    ("Eva Santos",      "eva@easytrip.pt"),
    ("Filipe Oliveira", "filipe@easytrip.pt"),
    ("Graça Lopes",     "graca@easytrip.pt"),
    ("Hugo Martins",    "hugo@easytrip.pt"),
    ("Inês Pereira",    "ines@easytrip.pt"),
    ("João Cunha",      "joao@easytrip.pt"),
]

print("\nA gerar SQL...")
sql = []

sql.append("-- ================================================================")
sql.append("-- seed.sql  —  EasyTrip / AirBus")
sql.append("-- gerado por seed.py a partir do dataset UrbanBus")
sql.append("-- utilizadores de teste: password = 1234567890")
sql.append("-- ================================================================\n")

sql.append("BEGIN;\n")

sql.append("-- limpar dados existentes")
for tbl in ["viagem", "titulo_zona", "titulo_transporte",
            "carreira_paragem", "paragem_zona", "horario",
            "carreira", "paragem", "zona_geografica",
            "veiculo", "utente"]:
    sql.append(f"TRUNCATE TABLE {tbl} RESTART IDENTITY CASCADE;")
sql.append("")

sql.append("-- Zonas geográficas")
sql.append("INSERT INTO zona_geografica (nome) VALUES")
sql.append("  ('Zona A - Centro'),")
sql.append("  ('Zona B - Suburbana'),")
sql.append("  ('Zona C - Periférica');\n")

sql.append(f"-- Paragens ({len(stops_df)})")
sql.append("INSERT INTO paragem (nome, estado, qr) VALUES")
rows = []
for i, row in stops_df.iterrows():
    code         = row["stop_code"]
    display_name = clean_stop(code)
    rows.append(f"  ('{esc(display_name)}', 'ATIVO', 'QR-{esc(display_name.upper())}')")
sql.append(",\n".join(rows) + ";\n")

sql.append("-- Paragem → Zona")
sql.append("INSERT INTO paragem_zona (paragem_id, zona_id) VALUES")
rows = []
for i, row in stops_df.iterrows():
    rows.append(f"  ({i + 1}, {zona_id(row['stop_code'], i)})")
sql.append(",\n".join(rows) + ";\n")

sql.append(f"-- Carreiras ({len(routes_list)})")
sql.append("INSERT INTO carreira (num, nome, estado) VALUES")
rows = []
for r in routes_list:
    num  = esc(r["num"])
    nome = esc(f"Linha {r['num']}")
    rows.append(f"  ('{num}', '{nome}', 'ATIVO')")
sql.append(",\n".join(rows) + ";\n")

total_veiculos = len(routes_list) * BUS_PER_ROUTE
sql.append(f"-- Veículos ({total_veiculos}, {BUS_PER_ROUTE} por carreira)")
sql.append("INSERT INTO veiculo (matricula, capacidade, ocupacao_atual, carreira_id) VALUES")
rows = []
for i in range(len(routes_list)):
    carreira_pk = i + 1
    for j in range(BUS_PER_ROUTE):
        letra = chr(ord('A') + j)
        rows.append(f"  ('EB-{carreira_pk:03d}-{letra}A', 50, 0, {carreira_pk})")
sql.append(",\n".join(rows) + ";\n")

cp_rows = []
for i, r in enumerate(routes_list):
    carreira_pk = i + 1
    ordem = 0
    for stop_code in r["stops"]:
        if stop_code in stop_pk:
            cp_rows.append(f"  ({carreira_pk}, {stop_pk[stop_code]}, {ordem})")
            ordem += 1

if cp_rows:
    sql.append(f"-- Carreira ↔ Paragem ({len(cp_rows)} associações)")
    sql.append("INSERT INTO carreira_paragem (carreira_id, paragem_id, ordem) VALUES")
    sql.append(",\n".join(cp_rows) + ";\n")
else:
    sql.append("-- AVISO: nenhuma associação carreira ↔ paragem gerada.\n")

sql.append(f"-- Utilizadores de teste ({len(test_users)})  |  password: 1234567890")
sql.append("INSERT INTO utente (nome, email, password) VALUES")
rows = [f"  ('{esc(n)}', '{esc(e)}', '{PWD}')" for n, e in test_users]
sql.append(",\n".join(rows) + ";\n")

print("A gerar horários...")
horario_rows = []
for i, r in enumerate(routes_list):
    carreira_pk = i + 1
    route_stops = [stop_pk[s] for s in r["stops"] if s in stop_pk]
    if not route_stops:
        continue

    services = []
    for day in range(1, 6):
        t = 6 * 60
        while t <= 22 * 60:
            services.append((day, t))
            t += 30
    for day in range(6, 8):
        t = 8 * 60
        while t <= 21 * 60:
            services.append((day, t))
            t += 45

    for day, dep_min in services:
        for j, paragem_pk in enumerate(route_stops):
            total = dep_min + j * 2
            hh, mm = (total // 60) % 24, total % 60
            horario_rows.append(
                f"  ({carreira_pk}, {paragem_pk}, '{hh:02d}:{mm:02d}:00', {day})"
            )

print(f"  {len(horario_rows):,} horários gerados.")

sql.append(f"-- Horários ({len(horario_rows):,} registos)")
for start in range(0, len(horario_rows), BATCH_SIZE):
    chunk = horario_rows[start:start + BATCH_SIZE]
    sql.append("INSERT INTO horario (carreira_id, paragem_id, hora_chegada, dia_semana) VALUES")
    sql.append(",\n".join(chunk) + ";\n")

sql.append("COMMIT;\n")
sql.append(
    f"-- RESUMO: {len(stops_df)} paragens | {len(routes_list)} carreiras "
    f"| {total_veiculos} veículos | {len(cp_rows)} associações "
    f"| {len(test_users)} utilizadores | {len(horario_rows):,} horários"
)

with open(OUT, "w", encoding="utf-8") as f:
    f.write("\n".join(sql))

print(f"\nSQL escrito em: {OUT}")
print(f"  Paragens      : {len(stops_df)}")
print(f"  Carreiras     : {len(routes_list)}")
print(f"  Assoc. CP     : {len(cp_rows)}")
print(f"  Utilizadores  : {len(test_users)}")
print(f"  Horários      : {len(horario_rows):,}")
print(f"\npara carregar na BD:")
print(f"  docker compose exec postgres psql -U postgres -d airbus -f /dev/stdin < data/seed.sql")
