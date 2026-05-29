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

The quickest path on a fresh Linux/macOS box (needs **Node ≥ 20**; `pnpm` is auto-provisioned
via corepack if missing):

```bash
make install   # prerequisites + dependencies + migrated & seeded database
make run       # start the API + web app in dev mode, then open the printed URL
```

On first launch you set a password (local single-user auth). `make help` lists every target:

| Target                  | Does                                                              |
| ----------------------- | ----------------------------------------------------------------- |
| `make install`          | check prerequisites, `pnpm install`, migrate + seed the SQLite DB |
| `make run` (`make dev`) | run the Fastify API and Vite web app together                     |
| `make build`            | production build of the web app                                   |
| `make test`             | run the full test suite                                           |
| `make reset-password`   | clear the local password (next launch re-runs setup)              |
| `make clean`            | remove dependencies, build output, and the local database         |

Equivalent manual commands:

```bash
pnpm install
pnpm db:migrate && pnpm db:seed
pnpm dev
```

See `docs/implementation-plan.md` for the full build plan and `docs/deviations.md` for any
deliberate deviations from it.
