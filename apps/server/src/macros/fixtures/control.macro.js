// ljspeed smoke-test macro (plan §8.2). MVP scope: this is the *only* macro in the codebase and
// it is never imported by app, auth, persistence, or any .svelte file — it exists purely to prove
// the ljspeed build-time compiler works end-to-end (`pnpm macro:check`). See docs/ljspeed.md.
import { defineMacro } from "@ljsp/macro-runtime";

// `unless(cond, body)` expands at compile time to `if (!(cond)) { (body)(); }`.
export const unless = defineMacro((ctx, condition, body) => {
  return ctx.syntax.statement`if (!(${condition})) { (${body})(); }`;
});
