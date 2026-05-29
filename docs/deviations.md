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

## 5. Phase 0 scope note

This commit delivers **Phase 0 — Bootstrap workspace** only. The `core` package ships two
real, test-driven helpers (`atf`, `ids`) to establish the Vitest baseline and prove the
`ljsp-core` / `nanoid` wiring. All other packages are scaffolded manifests; their real code
arrives in later phases per §16.
