import { EM } from "./data.js";
import { buildSeed } from "./transform.js";

// The prebuilt seed payload (people, notes, actions) the migration/seed script consumes.
export const seed = buildSeed(EM);

export { buildSeed, buildPeople, buildNotes, buildActions } from "./transform.js";
export { EM } from "./data.js";
