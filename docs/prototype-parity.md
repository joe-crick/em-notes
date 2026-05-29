# Prototype parity checklist (Phase 6)

Tracks the port of the React/Babel prototype (`reference/prototype/`) to Svelte, per plan §13.
Design direction = **Studio** (the screenshots' palette; see `docs/deviations.md` §13). Parity is
judged on recognisable visual structure and interaction affordances, not pixel perfection.

Status key: `[x]` done · `[~]` adapted (honest deviation, see notes) · `[ ]` not done.

## 13.1 Global parity

```text
[x] App uses the prototype visual language (Studio: warm cream, alpine-blue accent, Fraunces serif).
    NB: §13.1's prose ("slate, terracotta") describes the *other* direction (Conductor); Studio is
    the screenshot target. Full two-direction CSS is ported verbatim in app.css.
[x] EM Notes logo/wordmark match prototype proportions (atoms: Logo, Wordmark).
[x] Sidebar navigation matches prototype layout and active states.
[x] Topbar search/command affordance matches prototype placement.
[x] Cards, badges, rounded corners, spacing, and shadows are visually close (app.css verbatim port).
[x] No runtime tweak panel in production UI (tweaks-panel.jsx intentionally not ported).
[~] No mock AI feature presented as real. AICard renders a disabled placeholder stating AI isn't
    implemented locally; the prototype's fabricated "weekly briefing"/"signals"/"themes" copy is omitted.
```

## 13.2 Screenshot parity

```text
[x] Home          vs screenshots/home.png       — browser-verified
[x] Team          (layout from screens-home-team.jsx) — browser-verified
[x] Person        vs screenshots/02-person.png  — browser-verified
[x] Actions       vs screenshots/actions.png    — browser-verified (see Actions note)
[x] New note modal vs screenshots/01-new-note.png + 02-new-note.png — browser-verified
[x] Login/auth    (AuthScreen in screens-misc.jsx) — adapted to local password auth (§10, deviation 13)
[x] Settings      (SettingsScreen in screens-misc.jsx) — adapted (see Settings note)
```

## 13.3 Component parity

```text
[x] Logo            [x] Wordmark       [x] Icon          [x] Avatar
[x] Sparkline       [x] Bar            [x] SentimentDot  [x] Flag
[x] Kbd             [~] AICard (disabled placeholder only — no real local AI, per §13.3)
[x] Topbar          [x] Sidebar        [ ] CommandPalette (Phase 7)
[x] NewNoteModal    [x] AddReportModal
```

## 13.4 Data-source parity

```text
[x] Prototype seed data is visible after db:seed (6 people, 8 notes, 8 actions, sentiment).
[x] No component reads from window.EM — everything comes from the API stores.
[x] All displayed people/actions/notes are loaded from the API.
[x] Created records survive browser refresh (verified: new 1:1 note on Alex; new report "Priya Shah").
[x] Created records survive server restart (records are in SQLite; survive `start` cycles).
```

## Per-screen notes (honest deviations)

- **Home.** The prototype's hard-coded `TODAY_AGENDA`, `ME`, and AI "weekly briefing" aren't
  persisted entities. The agenda is rendered as **Upcoming 1:1s derived from each report's real
  `nextOneOnOne`**; PTO is derived from real `person.pto`; Team pulse + sentiment trend are computed
  from real `sentiment_points`. The greeting uses a fixed manager identity (`lib/manager.js`,
  deviation 13 — no `/api/me`). The AI briefing card is omitted (no mock AI).
- **Person.** Overview + 1:1 Notes are fully wired to the API (notes, embedded action items,
  sentiment, derived open-action count). **Goals / Feedback / Growth / Review prep** have schema
  tables reserved but are **not part of the MVP data model** (plan §9 tables exist; not seeded —
  deviation 9), so those tabs show an honest "not in this local build" empty state rather than the
  prototype's fabricated content.
- **Actions.** Driven by real `action_items` (`/api/actions`). The prototype's denormalized
  `OPEN_ACTIONS` fields (`due`/`from`/`urgent`/`overdue`) are not columns (deviation 9), so: due
  buckets are derived from `due_at` (seeded note-actions have none → they land in "No due date", so
  the header shows "0 overdue" rather than the screenshot's "1"); "from" shows the source kind
  (1:1 note vs Quick add). Toggling completion PATCHes `/api/actions/:id` (optimistic, reverts on
  failure). Quick-add posts a real team-wide action.
- **Settings.** Profile is the fixed manager identity. **Theme + Density are real**, persisted to
  `/api/settings` and applied to the `<body>` data-attributes app-wide. Keyboard shortcuts are
  listed (full handling is Phase 7). Calendar sync / AI / notifications are shown as explicitly
  "not part of this local build" instead of the prototype's mock toggles.
- **New note modal.** Prep context (carry-over, surfaced points) is built from real open actions +
  flags. Saving posts to `/api/people/:id/notes`; the discussion becomes the note `summary`,
  selected talking points become `highlights`, action items are embedded. The Record/transcription
  and AI-summary affordances are omitted (no mock AI). Dates entered via the date picker are stored
  in the seed's human format ("May 29, 2026") for consistency.
- **Add report modal.** Source → form → success. "Start blank" posts a real person to
  `/api/people`; the prototype's mocked directory + invite-by-email are shown as unavailable locally.

## 17.6 Acceptance (manual, browser-verified 2026-05-29)

```text
[x] Home screenshot parity accepted.
[x] Team layout parity accepted.
[x] Person screenshot parity accepted.
[x] Actions screenshot parity accepted (with the documented data deviation).
[x] New note modal screenshot parity accepted.
[x] Settings/auth visual parity accepted.
```
