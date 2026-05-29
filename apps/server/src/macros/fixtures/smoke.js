// ljspeed smoke-test input (plan §8.2). Compiling this with `ljspeed` expands the `unless` macro
// and emits smoke.out.js (+ .map); running the output with plain node must succeed. This is the
// whole MVP ljspeed surface — nothing else in the app depends on macros.
import { unless } from "./control.macro.js";

const user = { isAdmin: false };
let ran = false;

// After expansion this becomes: if (!(user.isAdmin)) { (() => { ran = true; })(); }
unless(user.isAdmin, () => {
  ran = true;
});

if (!ran) {
  console.error("ljspeed smoke FAILED: macro body did not execute");
  process.exit(1);
}

console.log("ljspeed smoke OK: unless() expanded and ran");
