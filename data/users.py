"""
users.py
gera SQL para inserir N utilizadores de carga com PASSE_ANUAL ativo.
também gera um CSV com credenciais para uso nos scripts k6.

uso:
    python data/users.py [N]   (default: 200)

output:
    data/users.sql  — SQL para inserir na BD
    data/users.csv  — email,password para k6

para carregar na BD:
    docker compose exec postgres psql -U postgres -d airbus -f /dev/stdin < data/users.sql
"""

import os
import sys

try:
    import bcrypt
except ImportError:
    print("instala bcrypt: pip install bcrypt")
    sys.exit(1)

N = int(sys.argv[1]) if len(sys.argv) > 1 else 200
PASSWORD = "1234567890"

print(f"A gerar hash BCrypt para '{PASSWORD}'...")
pwd_hash = bcrypt.hashpw(PASSWORD.encode(), bcrypt.gensalt(10)).decode()
print(f"  Hash: {pwd_hash}")

users = [(f"Load User {i:03d}", f"load_{i:03d}@test.pt") for i in range(1, N + 1)]

lines = []
lines.append(f"-- {N} utilizadores de carga (password: {PASSWORD})")
lines.append("")

lines.append("-- limpar dados de carga anteriores")
lines.append("DELETE FROM viagem")
lines.append("  WHERE utente_id IN (SELECT id FROM utente WHERE email LIKE 'load_%@test.pt');")
lines.append("DELETE FROM titulo_zona WHERE titulo_id IN (")
lines.append("  SELECT id FROM titulo_transporte WHERE utente_id IN (")
lines.append("    SELECT id FROM utente WHERE email LIKE 'load_%@test.pt'")
lines.append("  )")
lines.append(");")
lines.append("DELETE FROM titulo_transporte")
lines.append("  WHERE utente_id IN (SELECT id FROM utente WHERE email LIKE 'load_%@test.pt');")
lines.append("DELETE FROM utente WHERE email LIKE 'load_%@test.pt';")
lines.append("")

user_values = ",\n  ".join(
    f"('{nome}', '{email}', '{pwd_hash}')"
    for nome, email in users
)

lines.append("WITH new_users AS (")
lines.append(f"  INSERT INTO utente (nome, email, password) VALUES")
lines.append(f"  {user_values}")
lines.append("  RETURNING id")
lines.append("),")
lines.append("new_titles AS (")
lines.append("  INSERT INTO titulo_transporte")
lines.append("    (utente_id, data_aquisicao, estado, tipo_titulo, data_inicio, data_fim)")
lines.append("  SELECT id, NOW(), 'ATIVO', 'PASSE_ANUAL', '2026-01-01', '2027-12-31'")
lines.append("  FROM new_users")
lines.append("  RETURNING id")
lines.append(")")
lines.append("INSERT INTO titulo_zona (titulo_id, zona_id)")
lines.append("SELECT t.id, z.zona_id")
lines.append("FROM new_titles t")
lines.append("CROSS JOIN (VALUES (1), (2), (3)) AS z(zona_id);")

out_dir = os.path.dirname(__file__)
out_sql = os.path.join(out_dir, "users.sql")
out_csv = os.path.join(out_dir, "load_users.csv")

with open(out_sql, "w", encoding="utf-8") as f:
    f.write("\n".join(lines))

with open(out_csv, "w", encoding="utf-8") as f:
    f.write("email,password\n")
    for _, email in users:
        f.write(f"{email},{PASSWORD}\n")

print(f"SQL: {out_sql}")
print(f"CSV: {out_csv}")
print(f"\npara carregar:")
print(f"  docker compose exec postgres psql -U postgres -d airbus -f /dev/stdin < data/users.sql")