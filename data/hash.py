"""
hash.py
gera o hash BCrypt de uma password para usar no seed ou em testes.

uso:
    python data/hash.py
    python data/hash.py "outra_password"
"""

import sys
import bcrypt

password = sys.argv[1] if len(sys.argv) > 1 else "1234567890"
hashed = bcrypt.hashpw(password.encode(), bcrypt.gensalt(10)).decode()
print(f"password : {password}")
print(f"BCrypt   : {hashed}")