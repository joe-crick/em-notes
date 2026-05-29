import { empty$, rest } from "ljsp-core";

// Async transform flow: thread `value` through `fns` left to right, awaiting
// any async step. Mirrors the reference repo's `atf(value, ...fns)` helper.
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
