import argon2 from "argon2";

// Argon2id password hashing (hard constraint §2.7, §10). argon2's encoded hash string
// embeds the algorithm, params, and salt, so no separate salt column is needed.
export function hashPassword(plain) {
  return argon2.hash(plain, { type: argon2.argon2id });
}

export function verifyPassword(hash, plain) {
  return argon2.verify(hash, plain);
}
