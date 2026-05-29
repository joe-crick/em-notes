import { or_, refine_, Str, Num, Null, Undefined } from "ljspec";
import { tf, count, partial, lte$ } from "@em-notes/core";

// Optionality helpers. `shape_` requires every listed field to validate, and a missing
// field reads as `undefined` — so optional fields must explicitly allow `undefined`.
// These keep the spec files readable.

// May be absent entirely (key missing / undefined).
export const optional_ = (spec) => or_(spec, Undefined);

// Present but explicitly null.
export const nullable_ = (spec) => or_(spec, Null);

// Absent, null, or the value — the common "scalar that the prototype sometimes
// leaves as null and the DB stores as a nullable column" case.
export const maybe_ = (spec) => or_(spec, Null, Undefined);

// A string of at least `n` characters. `Str` guarantees string-ness; the refinement reads
// left-to-right (thread-first): take the value, `count` its length, then check the bound —
// `partial(lte$, n)` pre-binds n, so the threaded length lands as `lte$(n, length)`, i.e.
// `length >= n`.
export const minLenStr_ = (n) => refine_(Str, (v) => tf(v, count, partial(lte$, n)));

// Durations come from the prototype as "30 min" (string) but from the new-note modal as a
// number; the DB stores TEXT. Accept either and let the service coerce.
export const Duration = maybe_(or_(Str, Num));
