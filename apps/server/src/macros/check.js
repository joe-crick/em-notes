// `pnpm macro:check` — the ljspeed MVP smoke test (plan §8.2 acceptance).
//
// Steps: compile fixtures/smoke.js with the ljspeed CLI, assert smoke.out.js (+ .map) were
// emitted, run the output with plain node, and assert it exits 0. This is the only place ljspeed
// runs; it is deliberately NOT wired into Vite/Svelte (plan §8.1, §8.3).
import { spawnSync } from "node:child_process";
import { existsSync, rmSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const here = dirname(fileURLToPath(import.meta.url));
const fixtures = join(here, "fixtures");
const input = join(fixtures, "smoke.js");
const out = join(fixtures, "smoke.out.js");
const outMap = join(fixtures, "smoke.out.js.map");

function fail(message, hint) {
  console.error(`✗ macro:check: ${message}`);
  if (hint) console.error(`  ${hint}`);
  process.exit(1);
}

// Resolve the ljspeed CLI: prefer the workspace-installed bin, fall back to PATH.
function resolveLjspeed() {
  const candidates = [
    join(here, "..", "..", "..", "..", "node_modules", ".bin", "ljspeed"), // repo root .bin
    join(here, "..", "..", "node_modules", ".bin", "ljspeed"), // apps/server .bin
  ];
  for (const c of candidates) if (existsSync(c)) return c;
  return "ljspeed"; // assume on PATH
}

// Clean any stale output so the existence checks below are meaningful.
for (const f of [out, outMap]) if (existsSync(f)) rmSync(f);

const ljspeed = resolveLjspeed();
const compiled = spawnSync(ljspeed, [input], { stdio: "inherit" });

if (compiled.error) {
  fail(
    `could not run ljspeed (${compiled.error.code ?? compiled.error.message}).`,
    "Install it first: pnpm add -w -D github:joe-crick/ljspeed  (see docs/ljspeed.md)."
  );
}
if (compiled.status !== 0) fail(`ljspeed exited ${compiled.status} compiling smoke.js.`);

if (!existsSync(out)) fail("smoke.out.js was not emitted.");
if (!existsSync(outMap)) fail("smoke.out.js.map was not emitted.");

const ran = spawnSync(process.execPath, [out], { stdio: "inherit" });
if (ran.status !== 0) fail(`node smoke.out.js exited ${ran.status}.`);

console.log("✓ macro:check: ljspeed smoke test passed (smoke.out.js + .map emitted, ran clean).");
