# EM Notes Local-Only App — AI-Agent Implementation Plan v4

**Audience:** implementation agent / coding agent  
**Primary objective:** build a local-only, password-authenticated EM Notes app from the attached prototype.  
**Frontend constraint:** Svelte + Vite using **plain JavaScript**. Do not use TypeScript.  
**Backend recommendation:** **Node.js + Fastify + SQLite**.  
**Revision focus:** v4 integrates the agreed fixes for workspace layout, dependency naming/install coherence, Vite API proxy, strict `ljspeed`/Svelte boundaries, prototype parity acceptance checks, and explicit test coverage.

---

## 0. Non-negotiable decisions

Use this stack unless explicitly instructed otherwise:

```text
Frontend: Svelte + Vite + JavaScript
Backend: Node.js + Fastify + JavaScript ESM
Database: SQLite via better-sqlite3
Auth: Argon2id password hash + server-side sessions + HttpOnly SameSite cookie
Contracts: ljspec in a shared workspace package
Functional helpers: ljsp-core
Macros: ljspeed smoke-test-only for MVP; no .svelte macro compilation
Package manager: pnpm
Tests: Vitest
Formatter: Prettier
```

Backend language decision:

```text
Use Node/Fastify first.
Do not reopen Python vs Go during MVP.
Use Go later only if single-binary distribution becomes the dominant requirement.
```

Reason: the frontend is JavaScript, the contract/macro ecosystem is JavaScript-native, and the reference style repo is ESM JavaScript with pnpm, Vitest, Prettier, and `ljsp-core`.

---

## 1. Confirmed inspected inputs

### 1.1 Prototype archive: `EM Notes(1).zip`

Inspected files:

```text
EM Notes Redesign.html
app.jsx
atoms.jsx
data.js
screens-actions-note.jsx
screens-home-team.jsx
screens-misc.jsx
screens-person.jsx
styles.css
tweaks-panel.jsx
screenshots/01-new-note.png
screenshots/02-new-note.png
screenshots/02-person.png
screenshots/actions.png
screenshots/home.png
uploads/pasted-1779700445623-0.png
```

Prototype facts:

```text
- Static React/Babel design prototype, not production React.
- Mock data is exposed via window.EM from data.js.
- AuthScreen exists but is UI-only.
- Main route names: home, team, actions, person, settings.
- Main atoms: Logo, Wordmark, Icon, Avatar, Sparkline, Bar, SentimentDot, Flag, Kbd, AICard.
- Main screen/layout components: HomeScreen, TeamScreen, PersonScreen, ActionsScreen, SettingsScreen, NewNoteModal, AddReportModal, CommandPalette, Topbar, Sidebar.
- Tweak panel is a prototype-only utility. Do not port it as production functionality.
```

### 1.2 `jira-one-line-task-generator-main.zip`

Confirmed style and tooling:

```text
package manager: pnpm@9.0.0
module system: ESM, package.json has "type": "module"
file style: .mjs modules in the reference repo; use .js + ESM in this app unless a tool requires .mjs
formatter: Prettier
tests: Vitest
dependencies include: ljsp-core, axios, inquirer, dotenv
style: small named-export modules, direct composition, minimal ceremony
```

Style to copy:

```text
- Small modules with named exports.
- Functional composition where it improves clarity.
- `ljsp-core` helpers in data transforms.
- Async transform flow using an `atf(value, ...fns)` helper.
- `// prettier-ignore` before deliberately formatted transform pipelines.
- Co-located Vitest tests under src/**/tests.
```

Reference-style async flow:

```js
// prettier-ignore
await atf(
  getRuntimeConfig(),
  createUserPromptSpecs,
  runUserPrompts,
  validateUserInput,
  createJiraTicketSpecs,
  createJiraTickets,
  createMessages,
  logMessages
);
```

### 1.3 `ljspeed-master.zip`

Confirmed package/runtime facts:

```text
package name: ljspeed
version: 0.1.0
main/bin: bin/ljspeed
interface: CLI
source language: Rust
Node engine: >=16
postinstall: install/download/build binary
fallback requirement: Rust toolchain
```

Confirmed CLI behaviour:

```bash
ljspeed <input.js>
```

For `input.js`, output is:

```text
input.out.js
input.out.js.map
```

Confirmed compiler capabilities:

```text
- Parses JavaScript using SWC.
- Discovers imports from *.macro.js modules.
- Executes macro modules in an isolated Deno/V8 sandbox.
- Performs recursive outside-in fixed-point expansion.
- Respects lexical scope and variable shadowing.
- Supports named and namespace macro imports.
- Provides ctx.syntax.expression, ctx.syntax.statement, and ctx.syntax.program style syntax templates.
- Provides builder APIs such as ctx.gensym, ctx.block, ctx.call, and ctx.ident.
- Supports recur tail-call lowering to loops.
- Enforces conservative v11.2 safety rules for recur across try/catch/finally.
- Injects LJSP runtime imports for free variables such as map and filter.
- Emits source maps.
```

MVP implication:

```text
Use ljspeed only as a smoke-tested build-time compiler during MVP.
Do not wire ljspeed into the normal Vite/Svelte component path.
Do not place macro calls in .svelte files.
```

### 1.4 `ljsp-core` and `ljspec`

Known/required usage:

```text
- ljsp-core supplies functional helpers and should be used where it makes transforms clearer.
- ljspec supplies domain contracts/specs and must be the source of truth for request/domain validation.
- ljspec exports include sdef, isValid, conform, explain, gen, getSpec, fdef, instrument, getContract, and ContractError.
- Relevant spec tools include shape_, tuple_, and_, or_, literal_, enum_, oneOf_, instanceOf_, arrayOf_, refine_, where_, NonEmptyStr, Str, Num, Bool, Int, NonNegativeInt, PositiveInt, Fn, Null, Undefined, Date_, PlainObject, Buffer_, Any.
```

---

## 2. Hard implementation constraints

```text
1. Frontend must be Svelte + Vite + plain JavaScript.
2. Do not introduce TypeScript.
3. Backend should be Node.js + Fastify + JavaScript ESM.
4. Use pnpm workspaces.
5. Use SQLite for durable local storage.
6. Backend binds to 127.0.0.1 by default.
7. Use password auth with Argon2id and server-side sessions.
8. Use HttpOnly SameSite cookies; never store auth tokens in localStorage.
9. Use ljspec contracts as the domain/request validation source of truth.
10. Use ljsp-core where it improves clarity, especially in data transformations.
11. Match the reference repo style: ESM, small named-export modules, Vitest, Prettier, functional pipelines.
12. Preserve prototype visual design before making UI improvements.
13. No external AI calls in MVP.
14. Do not show mock AI recommendations as if implemented.
15. Do not use ljspeed in .svelte files for MVP.
```

---

## 3. Correct target repository layout

Important: `pnpm-workspace.yaml` must match the physical layout. All local packages must live under `packages/*`. Do **not** put `contracts/`, `core/`, or `prototype-seed/` at the repo root.

```text
em-notes/
  package.json
  pnpm-workspace.yaml
  .gitignore
  .prettierrc
  README.md
  docs/
    implementation-plan.md
    architecture.md
    contracts.md
    api.md
    local-security.md
    ljspeed.md
    prototype-parity.md
  reference/
    prototype/
      EM Notes Redesign.html
      app.jsx
      atoms.jsx
      data.js
      screens-actions-note.jsx
      screens-home-team.jsx
      screens-misc.jsx
      screens-person.jsx
      styles.css
      tweaks-panel.jsx
      screenshots/
  apps/
    web/
      package.json
      index.html
      vite.config.js
      src/
        main.js
        App.svelte
        app.css
        routes/
          Home.svelte
          Team.svelte
          Person.svelte
          Actions.svelte
          Settings.svelte
          Login.svelte
        components/
          atoms/
            Logo.svelte
            Wordmark.svelte
            Icon.svelte
            Avatar.svelte
            Sparkline.svelte
            Bar.svelte
            SentimentDot.svelte
            Flag.svelte
            Kbd.svelte
            AICard.svelte
          layout/
            Topbar.svelte
            Sidebar.svelte
            CommandPalette.svelte
          notes/
            NewNoteModal.svelte
            PrepTab.svelte
            NotesTab.svelte
            ActionsTab.svelte
            WrapTab.svelte
            ContextRail.svelte
          team/
            AddReportModal.svelte
            TeamPulseCard.svelte
        lib/
          api/
            client.js
            auth-api.js
            people-api.js
            notes-api.js
            actions-api.js
            settings-api.js
          stores/
            session.js
            route.js
            people.js
            actions.js
            ui.js
            settings.js
          keyboard/
            shortcuts.js
        tests/
          api-client.test.js
          route-store.test.js
    server/
      package.json
      src/
        main.js
        app.js
        config.js
        db.js
        migrations.js
        auth/
          auth-routes.js
          auth-service.js
          password.js
          sessions.js
          reset-password.js
        routes/
          people-routes.js
          notes-routes.js
          actions-routes.js
          settings-routes.js
          health-routes.js
        services/
          people-service.js
          notes-service.js
          actions-service.js
          settings-service.js
        repositories/
          people-repo.js
          notes-repo.js
          actions-repo.js
          settings-repo.js
        contracts/
          route-validation.js
        macros/
          compile-file.js
          fixtures/
            control.macro.js
            smoke.js
        tests/
          auth.test.js
          notes-routes.test.js
          actions-routes.test.js
      migrations/
        001_init.sql
        002_seed_from_prototype.sql
  packages/
    contracts/
      package.json
      src/
        index.js
        people.spec.js
        notes.spec.js
        actions.spec.js
        auth.spec.js
        settings.spec.js
        api.spec.js
        examples.js
        tests/
          people.spec.test.js
          notes.spec.test.js
          actions.spec.test.js
          auth.spec.test.js
          seed-conformance.test.js
    core/
      package.json
      src/
        atf.js
        ids.js
        dates.js
        result.js
        collections.js
        errors.js
        tests/
          atf.test.js
          ids.test.js
    prototype-seed/
      package.json
      src/
        data.js
        transform.js
        seed.js
        tests/
          transform.test.js
```

All project packages use `.js` files with ESM via `
"type": "module"` in each relevant `package.json`.

---

## 4. Package setup and dependency graph

### 4.1 Workspace file

`pnpm-workspace.yaml`:

```yaml
packages:
  - "apps/*"
  - "packages/*"
```

### 4.2 Dependency naming rule

Do not confuse local packages with external libraries:

```text
@em-notes/contracts  = local package containing app-specific ljspec contracts
@em-notes/core       = local package containing app utilities/helpers
@em-notes/prototype-seed = local package that transforms prototype data into seed data
ljspec               = external/spec library dependency used by @em-notes/contracts and server validation
ljsp-core            = external functional helper dependency used by @em-notes/core and app/server transforms
ljspeed              = external CLI macro compiler used only for MVP smoke test
```

### 4.3 Root `package.json`

```json
{
  "name": "em-notes-local",
  "private": true,
  "type": "module",
  "packageManager": "pnpm@9.0.0",
  "scripts": {
    "dev": "concurrently \"pnpm --filter @em-notes/server dev\" \"pnpm --filter @em-notes/web dev\"",
    "build": "pnpm -r build",
    "test": "pnpm -r test",
    "format": "prettier --write .",
    "db:migrate": "pnpm --filter @em-notes/server db:migrate",
    "db:seed": "pnpm --filter @em-notes/server db:seed",
    "macro:check": "pnpm --filter @em-notes/server macro:check"
  },
  "devDependencies": {
    "concurrently": "latest",
    "prettier": "latest",
    "vitest": "latest"
  }
}
```

Do not add ESLint, oxlint, and oxfmt simultaneously in MVP unless the agent copies exact configs from the reference repos and all tools pass on first setup. Fastest path is Prettier + Vitest first.

### 4.4 Package dependency graph

```text
apps/web
  depends on: @em-notes/contracts, @em-notes/core, svelte, vite, @vitejs/plugin-svelte

apps/server
  depends on: @em-notes/contracts, @em-notes/core, fastify, @fastify/cookie, better-sqlite3, argon2, nanoid

packages/contracts
  depends on: ljspec

packages/core
  depends on: ljsp-core, nanoid if IDs live here

packages/prototype-seed
  depends on: @em-notes/contracts, @em-notes/core

root devDependency
  ljspeed only if macro:check needs it installed through pnpm; otherwise use local/global CLI documented in docs/ljspeed.md
```

### 4.5 Install commands

Use these commands after creating all package manifests:

```bash
pnpm add --filter @em-notes/web @vitejs/plugin-svelte vite svelte
pnpm add --filter @em-notes/web @em-notes/contracts @em-notes/core

pnpm add --filter @em-notes/server fastify @fastify/cookie better-sqlite3 argon2 nanoid
pnpm add --filter @em-notes/server @em-notes/contracts @em-notes/core

pnpm add --filter @em-notes/contracts github:joe-crick/ljspec
pnpm add --filter @em-notes/core github:joe-crick/ljsp-core
pnpm add --filter @em-notes/prototype-seed @em-notes/contracts @em-notes/core

pnpm add -w -D concurrently prettier vitest
pnpm add -w -D github:joe-crick/ljspeed
```

If `github:joe-crick/ljspec`, `github:joe-crick/ljsp-core`, or `github:joe-crick/ljspeed` are not installable in the target environment, use the uploaded zips or local file dependencies and document the temporary path in `docs/implementation-plan.md`.

---

## 5. Vite dev proxy and API client

### 5.1 Required Vite proxy

Add the proxy immediately when bootstrapping `apps/web`; do not postpone it.

`apps/web/vite.config.js`:

```js
import { defineConfig } from "vite";
import { svelte } from "@vitejs/plugin-svelte";

export default defineConfig({
  plugins: [svelte()],
  server: {
    host: "127.0.0.1",
    port: 5173,
    proxy: {
      "/api": {
        target: "http://127.0.0.1:5174",
        changeOrigin: true,
      },
    },
  },
});
```

### 5.2 API client rule

All frontend API calls must use relative `/api` URLs and `credentials: "include"`.

`apps/web/src/lib/api/client.js`:

```js
export async function apiFetch(path, options = {}) {
  const response = await fetch(`/api${path}`, {
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {}),
    },
    ...options,
  });

  return await response.json();
}
```

Do not hard-code backend ports inside frontend API modules. The Vite proxy handles local development; later production serving can be handled by Fastify serving the built web assets.

---

## 6. Reference style guide

### 6.1 Module style

Use named exports and small modules:

```js
import { tf } from "ljsp-core";

export function buildThing(input) {
  // prettier-ignore
  return tf(
    input,
    normalizeInput,
    validateInput,
    finishThing
  );
}
```

### 6.2 Async flow helper

Implement `packages/core/src/atf.js`:

```js
import { empty$, rest } from "ljsp-core";

export async function atf(value, ...fns) {
  return await run(fns, value);
}

async function run(fns, result) {
  if (empty$(fns)) return result;
  const [fn] = fns;
  const next = isAsync(fn) ? await fn(result) : fn(result);
  return await run(rest(fns), next);
}

function isAsync(fn) {
  return fn?.constructor?.name === "AsyncFunction";
}
```

### 6.3 Test style

Use Vitest with co-located tests:

```text
src/foo.js
src/tests/foo.test.js
```

---

## 7. Contract strategy with `ljspec`

Create `packages/contracts` as the single source of truth for domain and API payload validation.

Initial specs:

```text
Person
CreatePersonInput
UpdatePersonInput
Note
CreateNoteInput
UpdateNoteInput
ActionItem
CreateActionInput
UpdateActionInput
LoginInput
SetupPasswordInput
Session
UserSettings
ApiError
ApiResult
```

Use `shape_`, `arrayOf_`, `enum_`, `literal_`, `NonEmptyStr`, `Str`, `Bool`, `Int`, `NonNegativeInt`, `Num`, and `Any` as appropriate.

Server route validation adapter:

```js
import { conform, explain } from "ljspec";

export function validateBody(spec, body) {
  const value = conform(spec, body);
  if (value === "::invalid") {
    return {
      ok: false,
      error: {
        code: "invalid_request",
        message: explain(spec, body),
      },
    };
  }

  return { ok: true, value };
}
```

Rule: do not scatter raw `::invalid` checks outside `route-validation.js` or contract tests.

---

## 8. `ljspeed` MVP boundary

### 8.1 Strict rule

For MVP:

```text
Do not compile .svelte files with ljspeed.
Do not place macro imports or macro calls in .svelte files.
Do not put ljspeed in the normal Vite/Svelte component path.
Do not make macros required for auth, persistence, sessions, password hashing, or route security.
```

Allowed MVP scope:

```text
- A smoke test fixture in apps/server/src/macros/fixtures.
- Optional pure JS utility experiments only after smoke test passes.
- Pure JS modules under packages/core/src/**, packages/contracts/src/**, apps/server/src/**, or apps/web/src/lib/**.
```

Explicitly disallowed for MVP:

```text
apps/web/src/**/*.svelte
apps/web/src/routes/**/*.svelte
apps/web/src/components/**/*.svelte
```

### 8.2 Smoke test only

Create:

```text
apps/server/src/macros/fixtures/control.macro.js
apps/server/src/macros/fixtures/smoke.js
```

Run:

```bash
ljspeed apps/server/src/macros/fixtures/smoke.js
node apps/server/src/macros/fixtures/smoke.out.js
```

Acceptance:

```text
smoke.out.js exists
smoke.out.js.map exists
node smoke.out.js succeeds
pnpm macro:check passes
```

`.gitignore`:

```gitignore
*.out.js
*.out.js.map
```

### 8.3 Do not implement Vite plugin in MVP

Do **not** implement `vite-plugin-ljspeed.js` in MVP. Document it as post-MVP only.

Reason: `ljspeed` emits sibling `.out.js` files and source maps. Wiring that into Vite/Svelte import resolution is unnecessary risk until there is a concrete macro use case.

---

## 9. Database design

Use SQLite with plain SQL migrations. Initial tables can follow this shape:

```sql
CREATE TABLE app_meta (
  key TEXT PRIMARY KEY,
  value TEXT NOT NULL,
  updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE auth_user (
  id TEXT PRIMARY KEY,
  password_hash TEXT NOT NULL,
  created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sessions (
  id TEXT PRIMARY KEY,
  user_id TEXT NOT NULL REFERENCES auth_user(id) ON DELETE CASCADE,
  expires_at TEXT NOT NULL,
  created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE people (
  id TEXT PRIMARY KEY,
  name TEXT NOT NULL,
  role TEXT NOT NULL,
  level TEXT,
  tenure TEXT,
  pronouns TEXT,
  timezone TEXT,
  initials TEXT NOT NULL,
  color TEXT,
  next_one_on_one TEXT,
  last_note_at TEXT,
  sentiment_label TEXT,
  pto TEXT,
  growth_focus TEXT,
  growth_progress REAL DEFAULT 0,
  tags_json TEXT NOT NULL DEFAULT '[]',
  flags_json TEXT NOT NULL DEFAULT '[]',
  created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sentiment_points (
  id TEXT PRIMARY KEY,
  person_id TEXT NOT NULL REFERENCES people(id) ON DELETE CASCADE,
  score INTEGER NOT NULL,
  recorded_at TEXT NOT NULL
);

CREATE TABLE notes (
  id TEXT PRIMARY KEY,
  person_id TEXT NOT NULL REFERENCES people(id) ON DELETE CASCADE,
  note_date TEXT NOT NULL,
  type TEXT NOT NULL,
  duration TEXT,
  sentiment INTEGER,
  summary TEXT NOT NULL,
  highlights_json TEXT NOT NULL DEFAULT '[]',
  transcript INTEGER NOT NULL DEFAULT 0,
  created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE action_items (
  id TEXT PRIMARY KEY,
  person_id TEXT REFERENCES people(id) ON DELETE SET NULL,
  note_id TEXT REFERENCES notes(id) ON DELETE SET NULL,
  text TEXT NOT NULL,
  owner TEXT NOT NULL DEFAULT 'me',
  done INTEGER NOT NULL DEFAULT 0,
  due_at TEXT,
  created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE goals (
  id TEXT PRIMARY KEY,
  person_id TEXT NOT NULL REFERENCES people(id) ON DELETE CASCADE,
  title TEXT NOT NULL,
  status TEXT NOT NULL DEFAULT 'active',
  progress REAL NOT NULL DEFAULT 0,
  created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE feedback (
  id TEXT PRIMARY KEY,
  person_id TEXT NOT NULL REFERENCES people(id) ON DELETE CASCADE,
  feedback_date TEXT NOT NULL,
  kind TEXT NOT NULL,
  body TEXT NOT NULL,
  created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE settings (
  key TEXT PRIMARY KEY,
  value_json TEXT NOT NULL,
  updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

Use JSON text columns for tags, flags, and note highlights in MVP.

---

## 10. Local authentication

Single-user local auth.

Endpoints:

```text
GET  /api/auth/status
POST /api/auth/setup
POST /api/auth/login
POST /api/auth/logout
```

Rules:

```text
- First run requires setup password.
- Password hash uses Argon2id.
- Sessions are stored in SQLite.
- Cookie is HttpOnly and SameSite=Strict.
- Cookie is Secure only when served over HTTPS.
- All non-auth API routes require a valid session.
- Password reset is a local CLI command that clears auth_user and sessions only.
```

Example status response:

```json
{
  "ok": true,
  "data": {
    "configured": true,
    "authenticated": false
  }
}
```

---

## 11. API design

Response envelope:

```js
{ ok: true, data }
```

or:

```js
{ ok: false, error: { code, message, details } }
```

Routes:

```text
GET    /api/health
GET    /api/auth/status
POST   /api/auth/setup
POST   /api/auth/login
POST   /api/auth/logout

GET    /api/people
POST   /api/people
GET    /api/people/:id
PATCH  /api/people/:id
DELETE /api/people/:id

GET    /api/people/:id/notes
POST   /api/people/:id/notes
GET    /api/notes/:id
PATCH  /api/notes/:id
DELETE /api/notes/:id

GET    /api/actions
POST   /api/actions
PATCH  /api/actions/:id
DELETE /api/actions/:id

GET    /api/settings
PATCH  /api/settings
```

MVP excludes cloud sync, remote sharing, calendar integrations, external AI calls, transcript parsing, and multi-user authorization.

---

## 12. Frontend implementation

### 12.1 Route store

Use a lightweight route store matching the prototype:

```js
{
  name: "home" | "team" | "actions" | "person" | "settings",
  personId: null | string
}
```

No full router is necessary for MVP unless deep links become a requirement.

### 12.2 Stores

Create:

```text
session.js
route.js
people.js
actions.js
settings.js
ui.js
```

### 12.3 Prototype conversion order

```text
1. styles.css -> app.css
2. atoms.jsx -> components/atoms/*.svelte
3. app.jsx -> App.svelte + Topbar + Sidebar + CommandPalette
4. screens-misc.jsx -> Login.svelte + Settings.svelte
5. screens-home-team.jsx -> Home.svelte + Team.svelte + AddReportModal.svelte
6. screens-person.jsx -> Person.svelte and person subcomponents
7. screens-actions-note.jsx -> Actions.svelte + NewNoteModal.svelte and note tabs
```

### 12.4 Keyboard behaviour

Preserve:

```text
- Cmd/Ctrl+K opens command palette.
- / opens command palette when not inside input/textarea/contenteditable.
- Escape closes palette/modals.
- Person navigation shortcuts if quick to port.
- New-note shortcut if quick to port.
```

---

## 13. Prototype parity checklist

This is mandatory. Do not redesign during the port. First copy layout/CSS faithfully; refactor only after parity is accepted.

Create `docs/prototype-parity.md` and maintain this checklist during Phase 6.

### 13.1 Global parity

```text
[ ] App uses the prototype visual language: slate, terracotta, manager-toolkit tone.
[ ] EM Notes logo/wordmark match prototype proportions closely.
[ ] Sidebar navigation matches prototype layout and active states.
[ ] Topbar search/command affordance matches prototype placement.
[ ] Cards, badges, rounded corners, spacing, and shadows are visually close.
[ ] No runtime tweak panel is present in production UI.
[ ] No mock AI feature is presented as a real implemented feature.
```

### 13.2 Screenshot parity checks

Compare each implemented screen against the prototype screenshots. Pixel perfection is not required, but the visual structure and interaction affordances must be recognisable.

```text
[ ] Home screen parity against screenshots/home.png
[ ] Team screen parity using prototype team layout from screens-home-team.jsx
[ ] Person screen parity against screenshots/02-person.png
[ ] Actions screen parity against screenshots/actions.png
[ ] New note modal parity against screenshots/01-new-note.png
[ ] New note modal secondary state parity against screenshots/02-new-note.png
[ ] Login/auth screen parity against AuthScreen in screens-misc.jsx
[ ] Settings screen parity against SettingsScreen in screens-misc.jsx
```

### 13.3 Component parity

```text
[ ] Logo
[ ] Wordmark
[ ] Icon
[ ] Avatar
[ ] Sparkline
[ ] Bar
[ ] SentimentDot
[ ] Flag
[ ] Kbd
[ ] AICard, disabled/placeholder only unless real local implementation exists
[ ] Topbar
[ ] Sidebar
[ ] CommandPalette
[ ] NewNoteModal
[ ] AddReportModal
```

### 13.4 Data-source parity

```text
[ ] Prototype seed data is visible after db:seed.
[ ] No component reads from window.EM.
[ ] All displayed people/actions/notes are loaded from the API.
[ ] Created/edited/deleted records survive browser refresh.
[ ] Created/edited/deleted records survive server restart.
```

---

## 14. Backend implementation

Fastify app factory:

```js
import Fastify from "fastify";
import cookie from "@fastify/cookie";

export function createApp({ db, config }) {
  const app = Fastify({ logger: true });

  app.register(cookie);

  app.get("/api/health", async () => ({ ok: true, data: { status: "ok" } }));

  return app;
}
```

Config env vars:

```text
EM_NOTES_DB_PATH=./data/em-notes.sqlite
EM_NOTES_HOST=127.0.0.1
EM_NOTES_PORT=5174
EM_NOTES_WEB_ORIGIN=http://127.0.0.1:5173
EM_NOTES_SESSION_DAYS=14
```

Layering:

```text
routes       = HTTP glue only
services     = validation orchestration, IDs, timestamps, cross-entity rules
repositories = SQL only
contracts    = ljspec specs and route validation
```

Use `better-sqlite3` synchronous DB calls. That is acceptable for a local single-user app and keeps code simple.

---

## 15. Prototype data mapping

### 15.1 Person

Prototype fields:

```text
id, name, role, level, tenure, pronouns, timezone, initials, color,
nextOneOnOne, lastNote, sentiment, sentimentLabel, flags, pto,
growthFocus, growthProgress, openActions, tags
```

Mapping:

```text
scalar fields -> people
sentiment array -> sentiment_points
openActions -> derive from action_items where done = 0
flags, tags -> JSON text columns for MVP
```

### 15.2 Note

Prototype fields:

```text
id, date, type, duration, sentiment, summary, highlights, actions, transcript
```

Mapping:

```text
scalar fields -> notes
highlights -> JSON text
actions -> action_items with note_id
```

---

## 16. Implementation phases

### Phase 0 — Bootstrap workspace

Deliver:

```text
- pnpm workspace using apps/* and packages/*.
- Root package.json scripts.
- apps/web, apps/server, packages/contracts, packages/core, packages/prototype-seed.
- Prettier and Vitest baseline.
```

Acceptance:

```bash
pnpm install
pnpm test
pnpm format
```

### Phase 1 — Contracts and seed

Deliver:

```text
- @em-notes/contracts with ljspec specs.
- Prototype data converted to ESM seed data.
- Seed conformance tests.
```

Acceptance:

```bash
pnpm --filter @em-notes/contracts test
pnpm --filter @em-notes/prototype-seed test
```

### Phase 2 — SQLite backend skeleton

Deliver:

```text
- Fastify app.
- SQLite opener.
- Migrations.
- Seed script.
- Health route.
```

Acceptance:

```bash
pnpm --filter @em-notes/server db:migrate
pnpm --filter @em-notes/server db:seed
pnpm --filter @em-notes/server dev
curl http://127.0.0.1:5174/api/health
```

### Phase 3 — Auth

Deliver:

```text
- setup/login/logout/status endpoints.
- Argon2id hashing.
- Session table.
- Auth middleware.
- Reset CLI.
```

Acceptance:

```text
- First run reports configured=false.
- Setup creates password hash, not plaintext password.
- Login sets HttpOnly cookie.
- Logout invalidates current session.
- Protected API calls fail when unauthenticated.
```

### Phase 4 — CRUD API

Deliver:

```text
- people routes.
- notes routes.
- actions routes.
- settings routes.
- contract-backed request validation.
```

Acceptance:

```text
- Seeded people load from API.
- Note creation persists.
- Action toggle persists.
- Invalid payload returns structured { ok:false, error }.
```

### Phase 5 — Svelte auth shell and Vite proxy

Deliver:

```text
- Vite dev proxy for /api -> http://127.0.0.1:5174.
- app starts at http://127.0.0.1:5173.
- setup/login screen.
- authenticated shell.
- topbar/sidebar.
```

Acceptance:

```text
- First run shows setup.
- Login shows app shell.
- Logout returns to login.
- Frontend API client uses relative /api paths and credentials: include.
```

### Phase 6 — Port screens with parity checklist

Deliver:

```text
- Home.
- Team.
- Person.
- Actions.
- Settings.
- New Note modal.
- Add Report modal.
- docs/prototype-parity.md completed.
```

Acceptance:

```text
- Screenshot parity checklist is completed.
- UI is visually close to prototype screenshots.
- All data comes from API, not window.EM.
- Created data persists after refresh and restart.
```

### Phase 7 — Command palette and shortcuts

Deliver:

```text
- Command palette.
- Route commands.
- New note command.
- Keyboard guard for form fields.
```

Acceptance:

```text
- Cmd/Ctrl+K opens palette.
- / opens palette outside inputs.
- Escape closes modal/palette.
- Commands navigate without full page reload.
```

### Phase 8 — ljspeed smoke check only

Deliver:

```text
- ljspeed installed or documented local CLI path.
- macro fixture.
- macro:check script.
- docs/ljspeed.md notes that .svelte compilation is intentionally excluded for MVP.
```

Acceptance:

```bash
pnpm macro:check
```

### Phase 9 — Test completion and hardening

Deliver:

```text
- Explicit test coverage listed in Section 17.
- Loading states.
- Empty states.
- Error states.
- README.
- local-security doc.
```

Acceptance:

```bash
pnpm test
pnpm build
pnpm format
```

---

## 17. Required test coverage

Add these tests before declaring MVP complete.

### 17.1 Contract tests

```text
[ ] Person valid example conforms.
[ ] Person invalid example fails.
[ ] CreatePersonInput rejects missing name.
[ ] Note valid example conforms.
[ ] Note invalid example fails.
[ ] CreateNoteInput rejects missing personId.
[ ] ActionItem valid example conforms.
[ ] CreateActionInput rejects empty text.
[ ] LoginInput rejects empty password.
[ ] Prototype seed data conforms to contracts.
```

### 17.2 Auth/session tests

```text
[ ] GET /api/auth/status returns configured=false on fresh DB.
[ ] POST /api/auth/setup rejects invalid password payload.
[ ] POST /api/auth/setup stores Argon2id hash, not plaintext.
[ ] POST /api/auth/login rejects invalid password.
[ ] POST /api/auth/login accepts valid password.
[ ] Login response sets HttpOnly cookie.
[ ] Authenticated GET /api/auth/status returns authenticated=true.
[ ] POST /api/auth/logout invalidates current cookie/session.
[ ] Expired session is rejected.
[ ] Unauthenticated protected API request returns 401 envelope.
```

### 17.3 Route validation tests

```text
[ ] POST /api/people rejects invalid payload.
[ ] PATCH /api/people/:id rejects invalid payload.
[ ] POST /api/people/:id/notes rejects invalid payload.
[ ] PATCH /api/notes/:id rejects invalid payload.
[ ] POST /api/actions rejects invalid payload.
[ ] PATCH /api/actions/:id rejects invalid payload.
[ ] Validation errors use { ok:false, error:{ code, message } }.
```

### 17.4 Persistence/repository tests

```text
[ ] Migration creates all expected tables.
[ ] Seed script inserts prototype people.
[ ] Seed script does not duplicate rows on repeat run unless explicitly reset.
[ ] People repo create/read/update/delete works.
[ ] Notes repo create/read/update/delete works.
[ ] Action repo create/toggle/delete works.
[ ] Deleting a person cascades or nulls related records according to schema.
[ ] Foreign key behavior is verified.
```

### 17.5 Frontend/API tests

```text
[ ] apiFetch prefixes /api and sends credentials: include.
[ ] apiFetch preserves caller headers.
[ ] session store handles unauthenticated status.
[ ] session store handles authenticated status.
[ ] route store navigates home/team/actions/person/settings.
[ ] command palette ignores / shortcut inside input/textarea/contenteditable.
```

### 17.6 Prototype parity acceptance tests

These can be manual checklist tests in `docs/prototype-parity.md` for MVP:

```text
[ ] Home screenshot parity accepted.
[ ] Team layout parity accepted.
[ ] Person screenshot parity accepted.
[ ] Actions screenshot parity accepted.
[ ] New note modal screenshot parity accepted.
[ ] Settings/auth visual parity accepted.
```

### 17.7 ljspeed tests

```text
[ ] macro:check creates .out.js and .out.js.map from smoke.js.
[ ] smoke.out.js runs successfully with node.
[ ] No .svelte file imports from *.macro.js.
[ ] No .svelte file contains macro-only syntax.
```

---

## 18. Backend code examples

Service style:

```js
import { assoc } from "ljsp-core";
import { atf } from "@em-notes/core/atf";
import { createId } from "@em-notes/core/ids";

export async function createNote({ notesRepo }, input) {
  // prettier-ignore
  return atf(
    input,
    addNoteId,
    addTimestamps,
    async (note) => await notesRepo.insert(note)
  );
}

function addNoteId(note) {
  return assoc(note, { id: createId("note") });
}

function addTimestamps(note) {
  const now = new Date().toISOString();
  return assoc(note, { createdAt: now, updatedAt: now });
}
```

Route style:

```js
import { CreateNoteInput } from "@em-notes/contracts";
import { validateBody } from "../contracts/route-validation.js";
import { createNote } from "../services/notes-service.js";

export function registerNoteRoutes(app, deps) {
  app.post("/api/people/:id/notes", async (request, reply) => {
    const validated = validateBody(CreateNoteInput, {
      ...request.body,
      personId: request.params.id,
    });

    if (!validated.ok) {
      return reply.code(400).send(validated);
    }

    const note = await createNote(deps, validated.value);
    return { ok: true, data: note };
  });
}
```

Frontend store style:

```js
import { writable } from "svelte/store";
import { apiFetch } from "../api/client.js";

export const people = writable([]);

export async function loadPeople() {
  const result = await apiFetch("/people");
  if (result.ok) people.set(result.data);
  return result;
}
```

---

## 19. MVP acceptance criteria

MVP is done when:

```text
1. User can set up a local password.
2. User can log in and log out.
3. User can view seeded team data from SQLite.
4. User can add/edit/delete a direct report.
5. User can create/edit/delete a 1:1 note.
6. User can create/toggle/delete action items.
7. User can view person profile, notes, actions, and growth data.
8. User can use command palette navigation.
9. Data persists across browser refresh and server restart.
10. API writes are validated by ljspec.
11. Code style follows the reference repo’s ESM/functional style.
12. Vite dev proxy works with cookie-based auth.
13. Prototype parity checklist is completed.
14. Required test coverage in Section 17 is implemented or explicitly documented as deferred with reason.
15. ljspeed macro smoke test passes or macro support is explicitly documented as disabled with reason.
16. pnpm test passes.
17. pnpm build passes.
18. pnpm format produces no unexpected large rewrites after final formatting.
```

---

## 20. Exact execution order for the implementation agent

```text
1. Bootstrap pnpm workspace with apps/* and packages/* only.
2. Copy prototype files into reference/prototype.
3. Create root scripts and package manifests.
4. Install dependencies using the corrected dependency graph.
5. Add Vite /api proxy before writing frontend API code.
6. Convert prototype data into packages/prototype-seed ESM data.
7. Implement ljspec contracts in packages/contracts.
8. Add contract and seed conformance tests.
9. Implement SQLite migrations and seed script.
10. Implement Fastify health/auth.
11. Implement CRUD API with contract validation.
12. Implement Svelte setup/login shell.
13. Port CSS and atoms faithfully.
14. Port layout and screens while updating docs/prototype-parity.md.
15. Wire frontend stores/API calls to backend.
16. Add command palette and keyboard shortcuts.
17. Add ljspeed smoke-only integration; do not touch .svelte files.
18. Add the required tests from Section 17.
19. Add loading/empty/error states.
20. Final README and docs.
```

Do not start with packaging, macro-heavy refactors, Vite macro plugins, or UI redesign.

---

## 21. Final stack summary

```text
Svelte/Vite JavaScript frontend
Fastify JavaScript backend
SQLite local database
Argon2id password hashing
HttpOnly SameSite session cookies
ljspec shared contracts through @em-notes/contracts
ljsp-core functional helpers through @em-notes/core
ljspeed smoke-test-only for MVP
pnpm workspaces using apps/* and packages/*
Vitest
Prettier
```

This plan is optimized for an AI implementation agent: it fixes the workspace/package coherence issues, makes the dev proxy explicit, keeps `ljspeed` out of Svelte for MVP, requires prototype parity tracking, and defines concrete test coverage before completion.
