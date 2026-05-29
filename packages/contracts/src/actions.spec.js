import { sdef, shape_, NonEmptyStr, Str, Bool } from "ljspec";
import { maybe_, optional_ } from "./helpers.js";

// Action item. Sourced from both note.actions ({id,text,done,owner}) and the prototype
// OPEN_ACTIONS list (which also carries due/dueDate/from/urgent — those ride along as
// extra keys). `personId`/`noteId` are the relational keys (normalized by the seed
// transform from the prototype's `person` field).
export const ActionItem = sdef(
  "ActionItem",
  shape_({
    id: NonEmptyStr,
    text: NonEmptyStr,
    done: Bool,
    owner: maybe_(Str),
    personId: maybe_(NonEmptyStr),
    noteId: maybe_(NonEmptyStr),
    dueAt: maybe_(Str),
  })
);

// Create input — only the text is required.
export const CreateActionInput = sdef(
  "CreateActionInput",
  shape_({
    text: NonEmptyStr,
    personId: maybe_(NonEmptyStr),
    noteId: maybe_(NonEmptyStr),
    owner: maybe_(Str),
    done: optional_(Bool),
    dueAt: maybe_(Str),
  })
);

// PATCH input — every field optional (the common case is toggling `done`).
export const UpdateActionInput = sdef(
  "UpdateActionInput",
  shape_({
    text: optional_(NonEmptyStr),
    owner: maybe_(Str),
    done: optional_(Bool),
    dueAt: maybe_(Str),
    personId: maybe_(NonEmptyStr),
    noteId: maybe_(NonEmptyStr),
  })
);
