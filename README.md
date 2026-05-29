# EM Notes — Local-Only App

A local-only, password-authenticated EM (Engineering Manager) Notes app, built from the
design prototype in `reference/prototype/`.

## Stack

| Layer     | Choice                                                          |
| --------- | --------------------------------------------------------------- |
| Frontend  | Svelte + Vite + plain JavaScript (no TypeScript)                |
| Backend   | Node.js + Fastify + JavaScript ESM                              |
| Database  | SQLite via `better-sqlite3`                                     |
| Auth      | Argon2id password hash + server-side sessions + HttpOnly cookie |
| Contracts | `ljspec` via `@em-notes/contracts`                              |
| Helpers   | `ljsp-core` via `@em-notes/core`                                |
| Macros    | `ljspeed` — smoke-test only for MVP                             |
| Tooling   | pnpm workspaces, Vitest, Prettier                               |

## Layout

```
apps/web        Svelte + Vite frontend
apps/server     Fastify + SQLite backend
packages/contracts        ljspec domain/API contracts (@em-notes/contracts)
packages/core             functional helpers (@em-notes/core)
packages/prototype-seed   prototype data -> seed data (@em-notes/prototype-seed)
reference/prototype       byte-faithful copy of the design prototype
docs/                     implementation plan + architecture notes
```

## Getting started

```bash
pnpm install
pnpm test
pnpm format
```

See `docs/implementation-plan.md` for the full build plan and `docs/deviations.md` for any
deliberate deviations from it.
