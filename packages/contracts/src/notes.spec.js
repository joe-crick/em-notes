import { sdef, shape_, arrayOf_, enum_, NonEmptyStr, Str, Bool } from "ljspec";
import { maybe_, optional_, Duration } from "./helpers.js";
import { SentimentScore } from "./people.spec.js";
import { ActionItem, CreateActionInput } from "./actions.spec.js";

// Note types offered by the new-note modal (screens-actions-note.jsx). Seed data uses
// "1:1" and "Skip", both covered here.
export const NoteType = sdef("NoteType", enum_("1:1", "Skip", "Career", "Retro", "Ad-hoc"));

// Full Note entity. `personId` is the relational key (the prototype keys notes by person
// in the NOTES object; the seed transform attaches it). `actions` are embedded in the
// prototype and validated here; the repository layer splits them into action_items.
export const Note = sdef(
  "Note",
  shape_({
    id: NonEmptyStr,
    personId: NonEmptyStr,
    date: NonEmptyStr,
    type: NoteType,
    summary: NonEmptyStr,
    duration: Duration,
    sentiment: maybe_(SentimentScore),
    highlights: optional_(arrayOf_(Str)),
    actions: optional_(arrayOf_(ActionItem)),
    transcript: maybe_(Bool),
  })
);

// Create input. `personId` is required (injected from the route param per plan §18);
// `summary` is required (DB NOT NULL).
export const CreateNoteInput = sdef(
  "CreateNoteInput",
  shape_({
    personId: NonEmptyStr,
    summary: NonEmptyStr,
    type: optional_(NoteType),
    date: maybe_(Str),
    duration: Duration,
    sentiment: maybe_(SentimentScore),
    highlights: optional_(arrayOf_(Str)),
    actions: optional_(arrayOf_(CreateActionInput)),
    transcript: maybe_(Bool),
  })
);

// PATCH input — every field optional.
export const UpdateNoteInput = sdef(
  "UpdateNoteInput",
  shape_({
    date: optional_(NonEmptyStr),
    type: optional_(NoteType),
    summary: optional_(NonEmptyStr),
    duration: Duration,
    sentiment: maybe_(SentimentScore),
    highlights: optional_(arrayOf_(Str)),
    transcript: maybe_(Bool),
  })
);
