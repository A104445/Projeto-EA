"""
truncate.py
cria versões truncadas dos ficheiros BUS_DATA para uso em desenvolvimento/testes.

uso:
    python data/truncate.py          # default 3 GB
    python data/truncate.py --gb 5   # 5 GB
"""

import os, argparse
import pandas as pd

BASE       = os.path.join(os.path.dirname(__file__), "urbanbus")
CHUNK_SIZE = 200_000

parser = argparse.ArgumentParser()
parser.add_argument("--gb", type=float, default=3.0,
                    help="tamanho alvo do ficheiro _trunc em GB (default: 3GB)")
args = parser.parse_args()

TARGET_B = int(args.gb * 1024 ** 3)

csv_files = sorted([
    f for f in os.listdir(BASE)
    if f.startswith("BUS_DATA") and f.endswith(".csv") and "_trunc" not in f
])

if not csv_files:
    print("Nenhum ficheiro BUS_DATA*.csv encontrado em", BASE)
    exit(1)

print(f"ficheiros encontrados: {csv_files}")
print(f"objetivo: {args.gb} GB por ficheiro\n")

for fname in csv_files:
    src  = os.path.join(BASE, fname)
    name = os.path.splitext(fname)[0]
    dst  = os.path.join(BASE, f"{name}_trunc.csv")

    src_size = os.path.getsize(src)
    print(f"{fname}  ({src_size / 1024**3:.1f} GB fonte)")

    if src_size <= TARGET_B:
        print("  → ficheiro já é mais pequeno que o alvo, a copiar por completo...")
        import shutil
        shutil.copy2(src, dst)
        print(f"  → {dst}")
        continue

    written_bytes  = 0
    total_rows     = 0
    header_written = False

    with open(dst, "w", encoding="utf-8", newline="") as out_f:
        for chunk in pd.read_csv(src, chunksize=CHUNK_SIZE, low_memory=False):
            csv_chunk = chunk.to_csv(index=False, header=not header_written)
            header_written = True
            encoded = csv_chunk.encode("utf-8")

            if written_bytes + len(encoded) > TARGET_B:
                rows_to_write = max(1, int(
                    len(chunk) * (TARGET_B - written_bytes) / len(encoded)
                ))
                partial = chunk.head(rows_to_write).to_csv(index=False, header=False)
                out_f.write(partial)
                total_rows    += rows_to_write
                written_bytes += len(partial.encode("utf-8"))
                break

            out_f.write(csv_chunk)
            total_rows    += len(chunk)
            written_bytes += len(encoded)

    final_mb = os.path.getsize(dst) / 1024**2
    print(f"  → {total_rows:,} linhas  |  {final_mb:.0f} MB  →  {dst}")

print("\nConcluído!")
print("podes agora correr:  python data/seed.py")
