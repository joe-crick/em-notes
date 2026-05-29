// @em-notes/contracts — ljspec domain & API contracts. Single source of truth for
// request/domain validation (plan §7). Specs are registered by name via `sdef`, so they
// can also be referenced as strings through ljspec's registry.

export { optional_, nullable_, maybe_, minLenStr_, Duration } from "./helpers.js";

export { SentimentScore, Person, CreatePersonInput, UpdatePersonInput } from "./people.spec.js";
export { ActionItem, CreateActionInput, UpdateActionInput } from "./actions.spec.js";
export { NoteType, Note, CreateNoteInput, UpdateNoteInput } from "./notes.spec.js";
export { LoginInput, SetupPasswordInput, Session } from "./auth.spec.js";
export { UserSettings } from "./settings.spec.js";
export { ApiError, ApiOk, ApiErr, ApiResult } from "./api.spec.js";

// Re-export the ljspec primitives callers need so app code imports from one place.
export { isValid, conform, explain, getSpec } from "ljspec";
