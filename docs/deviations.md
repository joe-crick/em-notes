# Deviations from `docs/implementation-plan.md` (v4)

The plan is followed precisely except for the items below, each of which is a documented,
necessary deviation (the plan itself permits documenting temporary/necessary deviations,
e.g. §4.5 for dependency install paths).

## 1. Svelte Vite plugin package name

- **Plan says:** `@vitejs/plugin-svelte` (§4.5, §5.1).
- **Reality:** that package does not exist on npm (`npm view @vitejs/plugin-svelte` returns
  nothing).
- **Used instead:** `@sveltejs/vite-plugin-svelte` — the official Svelte plugin for Vite.
- **Impact:** `apps/web/vite.config.js` imports `{ svelte } from "@sveltejs/vite-plugin-svelte"`.

## 2. pnpm version in `packageManager`

- **Plan says:** `"packageManager": "pnpm@9.0.0"` (§4.3).
- **Reality:** the build environment has pnpm `10.33.0` installed.
- **Used instead:** `"packageManager": "pnpm@10.33.0"` to keep `pnpm install` frictionless
  (avoids a corepack version-switch download). Workspace semantics are unchanged.

## 3. Dependency install method

- **Plan says:** install via a sequence of `pnpm add --filter ...` commands (§4.5), with
  GitHub specifiers `github:joe-crick/{ljspec,ljsp-core,ljspeed}`.
- **Used instead:** dependencies are declared directly in each package's `package.json`
  (matching the dependency graph in §4.4) and installed with a single `pnpm install`. The
  GitHub specifiers are used as written and are reachable in this environment. End state is
  identical to running the `pnpm add` sequence.

## 4. Per-package `vitest` devDependency

- `vitest` is declared as a devDependency in each workspace package (in addition to the root)
  so that `pnpm -r test` resolves the `vitest` binary deterministically inside every package.
  Packages without tests yet use `vitest run --passWithNoTests` so the recursive test command
  is green during early phases.

## 5. Seed-conformance test location (Phase 1)

- **Plan says:** `seed-conformance.test.js` lives under `packages/contracts/src/tests/` (§3 layout).
- **Used instead:** it lives in `packages/prototype-seed/src/tests/seed-conformance.test.js`.
- **Reason:** the test must import both the contracts and the built seed. Putting it in
  `@em-notes/contracts` would make contracts depend on `@em-notes/prototype-seed`, which
  already depends on contracts — a circular workspace dependency. The seed package proving
  its own output against the contracts is the natural, acyclic placement. The §17.1 check
  ("prototype seed data conforms to contracts") is fully covered.

## 6. ljsp-core / ljspec accessed via local packages (Phase 1)

- **Plan §18** shows app code importing `ljsp-core` (and `ljspec`) directly, but the
  dependency graph **§4.4** lists `@em-notes/prototype-seed`'s deps as only
  `@em-notes/contracts` and `@em-notes/core` (no `ljsp-core`/`ljspec`).
- **Used instead:** to honor the graph and pnpm's strict `node_modules`, `@em-notes/core`
  re-exports the needed `ljsp-core` helpers via `core/src/collections.js`, and
  `@em-notes/contracts` re-exports `conform`/`isValid`/`explain`/`getSpec` from `ljspec`.
  Transforms import `assoc`/`map` from `@em-notes/core`; tests import `conform` from
  `@em-notes/contracts`. No package imports `ljsp-core`/`ljspec` it doesn't declare.
- **Note:** when the server is built (Phase 2+), §18's direct `import { assoc } from "ljsp-core"`
  will be revisited the same way (route via `@em-notes/core`) unless `ljsp-core` is added to
  the server's direct deps.
- **Update (Phase 1 review):** `@em-notes/contracts` now also depends on `@em-notes/core` so
  `helpers.js` can compose its refinements from `ljsp-core` primitives (`tf`, `count`,
  `partial`, `lte$`). §4.4 lists contracts' dep as `ljspec` only; the extra `→ core` edge is
  a deliberate choice to keep validation logic in the reference repo's functional/composed
  style (§6, §10 #10). Acyclic: `contracts → core → ljsp-core`.

## 7. No unit tests for ljspec specs (Phase 1 review)

- **Plan §17.1** lists per-spec contract tests ("Person valid example conforms", "Note
  invalid example fails", etc.).
- **Used instead:** those were removed at the user's direction. An ljspec spec **is** an
  executable contract; asserting that a valid example conforms / an invalid one fails just
  re-tests the ljspec library, not our code. `examples.js` (which only fed those tests) was
  removed with them.
- **What is tested instead (real code):** the prototype-seed `transform` logic
  (`transform.test.js`) and that the **actual seed data** conforms to the contracts
  (`seed-conformance.test.js`) — i.e. §17.1's one meaningful check, "prototype seed data
  conforms to contracts." Spec correctness is exercised transitively through that real data.

## 8. Seed via JS, not `002_seed_from_prototype.sql` (Phase 2)

- **Plan §3 layout** lists `migrations/002_seed_from_prototype.sql`.
- **Used instead:** seeding is a JS script, `apps/server/src/seed.js` (`db:seed`), that consumes
  `@em-notes/prototype-seed`. `migrations/001_init.sql` holds the schema only.
- **Reason:** a hand-written SQL seed file would duplicate the prototype data and bypass the
  contract-conformed `@em-notes/prototype-seed` package. `migrate` (schema) and `seed` (data)
  stay separate, matching the two §16 acceptance commands. Seeding is idempotent via
  `INSERT OR IGNORE` on the stable prototype ids.

## 9. Phase 2 seed scope & synthesized data

- **action_items** are seeded from the **note-linked** actions (`note.actions`, carrying
  `note_id`/`person_id`) per §15.2 — not from the prototype `OPEN_ACTIONS` dashboard list.
  `OPEN_ACTIONS` is a denormalized projection (due-date labels, team-wide items); the
  Actions/Home screens will derive the open list from `action_items WHERE done = 0` later.
  `buildActions` (the `OPEN_ACTIONS` projection) is retained for that future use.
- **sentiment_points** need a `recorded_at`, but the prototype sentiment arrays have scores
  only. The seed synthesizes weekly timestamps backward from a fixed base (`2026-05-22`) so
  seeding is deterministic.
- **feedback** and **goals** tables are created (full §9 schema) but **not seeded yet** — the
  prototype has the data, but it isn't contract-backed and isn't required by Phase 2. Deferred.

## 10. Auth additions (Phase 3)

- **`auth/auth-guard.js`** is not in the §3 file list but is required to implement §10's "all
  non-auth API routes require a valid session." It registers an `onRequest` hook that 401s
  any `/api/*` route outside a small public allowlist (`/api/health`, `/api/auth/*`). It
  parses the cookie via `app.parseCookie(...)` rather than `request.cookies`, because
  `@fastify/cookie` populates `request.cookies` from its own `onRequest` hook and hook
  ordering is not guaranteed.
- **Setup auto-signs-in:** `POST /api/auth/setup` creates the user *and* establishes a
  session/cookie, so first-run users land straight in the app. (`POST /api/auth/login`
  remains separate and is exercised independently.)
- **`auth:reset` script** added to `apps/server/package.json` for the §10/§16 reset CLI
  (`src/auth/reset-password.js`), which clears `auth_user` + `sessions` only.
- Cookie is `HttpOnly; SameSite=Strict; Path=/`, `Secure` only when `request.protocol` is
  https (§10) — so it's non-Secure on local http, as intended.

## 11. PATCH handling vs ljspec `conform` (Phase 4)

`conform(shape_, body)` returns the value with **every** declared field present — optional
fields that were absent come back as `undefined` (shape conform spreads the input, then sets
each field's conformed value). So a `PATCH` body of `{ done: true }` validated against
`UpdateActionInput` yields `{ done: true, text: undefined, owner: undefined, ... }`.

The dynamic UPDATE builders in the repos therefore test `patch[field] !== undefined` (not
`field in patch`) when deciding which columns to write. This both fixes a NOT-NULL crash
(writing `text = NULL` on a partial update) and gives the right semantics: an omitted field
is left unchanged, while an explicit `null` is written as a "clear."

## 12. Phase 4 layering notes

- Cross-entity read assembly lives in **services**, not repos: `people-service` attaches the
  sentiment trend (from `people-repo.sentimentByPerson`) and derives `openActions` (from
  `actions-repo.openCountByPerson`, §15.1); `notes-service` attaches each note's
  `action_items`. Repos stay single-table (+ their row↔domain mapping).
- Create flows use `@em-notes/core`'s `atf` + `assoc` + `createId` (the §18 style):
  `atf(input, addId, persist)`. Audit columns (`created_at`/`updated_at`) are left to the
  schema defaults / `CURRENT_TIMESTAMP` and are not part of the domain shapes (the contracts
  don't include them).

## 13. Phase 5 frontend (Svelte auth shell)

- **Design direction = Studio** (user decision). The `home.png`/`02-person.png` screenshots use
  Studio tokens (round avatars, pill nav, warm cream, alpine-blue accent, Fraunces serif), and
  the prototype HTML defaulted to `data-direction="studio"`. This is what §13.2 screenshot
  parity is judged against. §13.1's prose ("slate, terracotta, manager-toolkit") describes the
  other direction (Conductor) and is treated as not-the-target. `index.html` sets
  `data-direction="studio"`. The full two-direction CSS is ported verbatim, so flipping is a
  one-attribute change if ever wanted.
- **Login adapts the prototype AuthScreen** (which is OAuth UI-only: "Continue with Google").
  Our auth is local single-user password (§10), so the left column hosts a password field —
  setup (first run, `configured=false`) or sign-in — keeping the two-column editorial layout.
- **`apiFetch` (client.js) differs from the §5.2 snippet in two ways**, both fixes:
  1. Spreads `...options` **before** `headers` so the merged headers win (the snippet spreads
     options last, dropping `Content-Type` when a caller passes its own `headers`).
  2. Sets `Content-Type: application/json` **only when a body is present**. A bodyless POST
     (logout, future DELETEs) sent with that header is rejected by Fastify as an empty JSON
     body — which silently broke logout until fixed. Verified end-to-end in the browser.
- **Topbar manager identity is a fixed `{ initials: "JC" }`** — single-user local app with no
  `/api/me` endpoint. Revisit if a profile endpoint is added.
- **Main content is a per-route placeholder** in Phase 5; the real Home/Team/Person/Actions/
  Settings screens are Phase 6. The shell, sidebar (live API data), routing, and full auth
  cycle (setup → shell → logout → login) are working and browser-verified.

## 14. Phase 0 scope note

This commit delivers **Phase 0 — Bootstrap workspace** only. The `core` package ships two
real, test-driven helpers (`atf`, `ids`) to establish the Vitest baseline and prove the
`ljsp-core` / `nanoid` wiring. All other packages are scaffolded manifests; their real code
arrives in later phases per §16.

## 15. Phase 6 screen port (parity vs. honest data)

The full checklist lives in `docs/prototype-parity.md`; the headline deviations:

- **No fabricated data presented as real (plan §1.1, §13.1).** The prototype hard-codes a lot of
  demo content (today's agenda, AI briefings/signals/themes, feedback, goals, growth radars,
  denormalized action due-dates). Where there's no backing entity/endpoint, the port either
  **derives from real data** (Home agenda from `person.nextOneOnOne`; PTO from `person.pto`; team
  pulse + trends from `sentiment_points`) or shows an **honest empty/placeholder state**
  (Person Goals/Feedback/Growth/Review tabs; AICards). It never renders invented records.
- **`AICard` is a disabled placeholder** (§13.3) — there is no local AI; it says so.
- **Actions screen** runs off real `action_items`; the prototype's `OPEN_ACTIONS`
  `due/from/urgent/overdue` aren't columns (see §9 above), so due buckets come from `due_at`
  (seeded note-actions have none → "0 overdue", "No due date") and "from" shows the source kind.
- **Settings** ports only what's real: Profile (fixed manager identity, deviation 13), and
  **Theme + Density** persisted via `/api/settings`. Calendar/AI/notifications are labelled
  not-implemented rather than shown as working mock toggles.
- **New note dates** entered via the date picker are stored in the seed's human format
  ("May 29, 2026") for display consistency; `notes-repo.listNotesByPerson` now orders
  **newest-first** (`created_at DESC, rowid`) so freshly-saved notes surface at the top.
- **Manager identity** is a shared constant (`apps/web/src/lib/manager.js`) used by the Topbar,
  Home greeting, and Settings — single-user local app, no `/api/me` (continues deviation 13).
- **Dark-theme fix:** exposing the Settings theme toggle surfaced a token bug — `.btn-primary`
  uses `--fg-1` as its background, which is light in dark themes, so its `--fg-on-dark` label
  vanished. Added `[data-theme="dark"] .btn-primary { color: var(--bg-page) }`. Studio/light
  remains the parity target; dark is the companion palette.

## 16. Phase 7 command palette + keyboard shortcuts

Ported faithfully from `app.jsx` with these notes:

- **Keyboard handling lives in `apps/web/src/lib/keyboard/shortcuts.js`** (a single
  `handleKeydown` attached via `<svelte:window>` in App), replacing Phase 5/6's inline
  Escape-only handler. It reads stores with `get()` to avoid stale closures (the prototype used
  refs for the same reason). Shortcuts: Cmd/Ctrl+K toggle palette (works even in inputs); `/`
  open palette; `n` new note (for the routed person, else opens the palette to pick one); `g`
  leader → `h`/`t`/`a`/`s` and `1–9` (jump to the Nth direct report); Escape closes the topmost
  overlay. A guard disables non-Cmd+K shortcuts while typing in input/textarea/select/
  contenteditable or while a modal/palette owns focus.
- **Added `g a` → Actions** (not in the prototype's `g h/t/s`) for symmetry with the four nav
  routes; `g 1–9` instead of the prototype's hard-capped `g 1–6` (works for any roster size).
- **`CommandPalette.svelte`** reads the live `people` store (no `window.EM`); commands cover the
  four routes, jump-to-person, "New 1:1 note → {person}", and Sign out. The prototype's
  ambiguous first "New 1:1 note…" item (which silently picked `TEAM[0]`) is dropped in favour of
  the explicit per-person entries. Arrow/Enter are handled inside the palette input; Escape is
  handled by the global handler.

## 17. Post-Phase-7 code-review hardening

A review surfaced correctness/data-integrity holes; fixes (all with regression tests):

1. **Note create no longer 500s on a minimal body.** `CreateNoteInput` allows `type`/`date` to be
   absent but the `notes` columns are NOT NULL. `notes-service.createNote` now defaults
   `type → "1:1"` and `date → today` (human format) before persisting.
2. **Note create is transactional.** The note insert + its embedded action inserts run inside one
   `db.transaction(...)`, so a failed action can't leave a half-written note.
3. **Action FK refs are validated in the service.** `createAction`/`updateAction` check that any
   `personId`/`noteId` exists and return a structured `400 invalid_reference` instead of letting
   SQLite's foreign-key constraint throw a 500. Both functions now return a discriminated result
   (`{ ok, data } | { ok:false, error } | { ok:false, notFound }`); the routes map it.
4. **Notes store stale-response guard.** `loadNotes` stamps each call with an incrementing token
   and only commits the response if it's still the latest, fixing the A→B navigation race.
5. **`apiFetch` normalizes failures.** Transport errors and non-JSON/empty bodies return a
   structured envelope (`network_error` / `bad_response`) instead of throwing into callers.
6. **Login throttling.** Consecutive failed logins incur an escalating in-memory delay
   (250 ms × attempts, capped at 2 s), reset on success. Local single-user app, so in-memory is
   sufficient; the server binds to 127.0.0.1 by default.
7. **Settings merge is safe + validated.** A single-field PATCH no longer wipes the other field
   (the conform-fills-undefined keys are stripped before merge), and the *merged* result is
   validated against `UserSettings` before saving.
8. **Domain stores cleared on logout.** `logout()` resets the `people`/`actions`/`notes`/
   `settings` stores so no local data lingers in memory after sign-out.
9. **Destructive deletes — resolved.** A delete affordance now exists (Person view → Delete) and
   is gated behind an explicit two-step confirm modal warning about the cascade
   (`DeletePersonModal`). Hard-delete is retained (no soft-delete) for the local MVP; the
   confirm + the warning copy are the safeguard.

## 18. Team rename + member edit/delete (post-MVP UX)

- **Team name** is now editable (Settings → Team), stored as `teamName` on `UserSettings`
  (non-empty optional) — no schema change since settings are a JSON blob. Defaults to `ME.team`.
- **Edit member** (`EditPersonModal`, opened from the Person header) PATCHes `/api/people/:id`.
- **Delete member** (`DeletePersonModal`, Person header, confirm-gated) DELETEs `/api/people/:id`.

## 19. Calendar sync (extends the MVP boundary)

The plan (§13) marked calendar sync out-of-scope and Settings showed it as "not implemented".
At the user's request it's now a real feature, **read-only feed-URL subscription** (the chosen
mechanism — no OAuth, no stored credentials):

- **Migration 002** adds `people.email`, `calendar_feeds`, and `calendar_events`. `email` flows
  through the Person contracts + Add/Edit forms + seed, and is the primary key for matching.
- **`apps/server/src/calendar/ics.js`** is a dependency-free iCalendar reader (chosen over an npm
  ICS lib partly to avoid the external-install friction seen with ljspeed). Scope: VEVENT
  SUMMARY/LOCATION/UID/DTSTART/DTEND/ATTENDEE/RRULE; recurrence expansion for
  FREQ=DAILY|WEEKLY|MONTHLY with INTERVAL/COUNT/UNTIL and (WEEKLY) BYDAY. **Times are wall-clock**:
  `Z` is true UTC; floating/`TZID` values keep their wall-clock time but are not zone-converted
  (a documented simplification — fine for surfacing an agenda; full VTIMEZONE handling is a
  follow-up).
- **`calendar-service`** fetches each feed (Node `fetch`, 10 s timeout, http/https only), expands a
  −1d…+30d window, matches attendees → reports (**email, then attendee CN, then person-name in the
  summary**), caches event instances per feed, and **auto-creates a prep note** for each matched
  event in the next 24 h (dedup by person + date). The fetch is separated from the
  parse→match→cache→prep core (`applyFeedEvents(db, feed, icsText, nowMs)`) so the pipeline is
  testable without network.
- **Routes** (`/api/calendar/feeds` CRUD, `/sync`, `/agenda`) sit behind the auth guard. `main.js`
  syncs on boot and every 15 min (`setInterval`, unref'd).
- **Frontend**: Settings → Calendar manages feeds (add/list/remove/refresh, last-synced/error);
  Home's agenda shows real upcoming events when any feed is connected, falling back to the derived
  `nextOneOnOne` list otherwise.
- **Security note**: the local server makes outbound requests to user-supplied feed URLs. That's
  inherent to the feature and acceptable for a single-user local app (the user controls the URLs);
  it is scoped to http/https. The server still binds to 127.0.0.1 by default.
