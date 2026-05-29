# ljspeed (MVP boundary + smoke test)

`ljspeed` is the external Rust CLI macro compiler for the LJSP ecosystem. In this project it is
used **only** as a build-time smoke test (plan §8). It is intentionally **not** part of the normal
Vite/Svelte build.

## Strict MVP rules (plan §8.1)

- Do **not** compile `.svelte` files with ljspeed, or place macro imports/calls in them.
- Do **not** put ljspeed in the Vite/Svelte component path (no `vite-plugin-ljspeed` in MVP, §8.3).
- Do **not** make macros required for auth, persistence, sessions, password hashing, or route
  security. The app runs fully without ljspeed ever being installed.
- The only macro in the repo is the smoke fixture below.

## Files

```text
apps/server/src/macros/fixtures/control.macro.js   # defines the `unless` macro
apps/server/src/macros/fixtures/smoke.js           # imports + uses it
apps/server/src/macros/check.js                    # `pnpm macro:check` runner
```

Generated outputs (`smoke.out.js`, `smoke.out.js.map`) are git-ignored (`*.out.js`,
`*.out.js.map`).

## Install

ljspeed is a Rust binary distributed from GitHub; its postinstall downloads/builds the binary (a
Rust toolchain is the documented fallback). It is **not** a default dependency — installing it runs
build scripts from an external source, so it is opt-in:

```bash
pnpm add -w -D github:joe-crick/ljspeed
```

`ljspeed` is listed in `pnpm-workspace.yaml` `onlyBuiltDependencies` so pnpm 10 will run its build
script. If the prebuilt binary can't be fetched, the postinstall builds from source via the local
Rust toolchain (`cargo`/`rustc`).

## Run the smoke test

```bash
pnpm macro:check
```

This compiles `smoke.js` with ljspeed, asserts `smoke.out.js` and `smoke.out.js.map` were emitted,
then runs the output with plain node and asserts it exits 0 (plan §8.2 acceptance). If ljspeed
isn't installed, `macro:check` fails with an actionable message rather than silently passing.

Equivalent manual steps:

```bash
ljspeed apps/server/src/macros/fixtures/smoke.js
node   apps/server/src/macros/fixtures/smoke.out.js   # prints: ljspeed smoke OK
```

## Macro API used

Per the ljspeed README, a macro is authored with `defineMacro` from `@ljsp/macro-runtime` and
returns a syntax template:

```js
import { defineMacro } from "@ljsp/macro-runtime";
export const unless = defineMacro((ctx, condition, body) =>
  ctx.syntax.statement`if (!(${condition})) { (${body})(); }`
);
```

The consumer calls it like a function; ljspeed expands and removes the macro import at compile time:

```js
import { unless } from "./control.macro.js";
unless(user.isAdmin, () => { /* runs only when not admin */ });
```

## Post-MVP

A `vite-plugin-ljspeed` (resolving the sibling `.out.js`/`.map` emit into Vite) is explicitly out
of scope for MVP (§8.3) and should only be considered once there's a concrete macro use case.
