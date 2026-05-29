import {
  sdef,
  shape_,
  arrayOf_,
  refine_,
  NonEmptyStr,
  Str,
  Num,
  Int,
  NonNegativeInt,
} from "ljspec";
import { maybe_, optional_ } from "./helpers.js";

// Sentiment is a 1–5 score (prototype sentiment arrays + per-note sentiment).
export const SentimentScore = sdef(
  "SentimentScore",
  refine_(Int, (v) => v >= 1 && v <= 5)
);

// Full Person entity. Field names follow the prototype `data.js` (camelCase); see
// docs/deviations.md and the contract-basis decision. Extra keys pass through `shape_`
// untouched, so prototype-only display fields ride along without being enumerated here.
export const Person = sdef(
  "Person",
  shape_({
    id: NonEmptyStr,
    name: NonEmptyStr,
    role: NonEmptyStr,
    initials: NonEmptyStr,
    email: maybe_(Str),
    level: maybe_(Str),
    tenure: maybe_(Str),
    pronouns: maybe_(Str),
    timezone: maybe_(Str),
    color: maybe_(Str),
    nextOneOnOne: maybe_(Str),
    lastNote: maybe_(Str),
    sentiment: optional_(arrayOf_(SentimentScore)),
    sentimentLabel: maybe_(Str),
    flags: optional_(arrayOf_(Str)),
    pto: maybe_(Str),
    growthFocus: maybe_(Str),
    growthProgress: maybe_(Num),
    // Derived in the app from open action_items (plan §15.1); kept optional on the entity.
    openActions: maybe_(NonNegativeInt),
    tags: optional_(arrayOf_(Str)),
  })
);

// Input for creating a direct report. Server assigns the id; initials may be derived.
export const CreatePersonInput = sdef(
  "CreatePersonInput",
  shape_({
    name: NonEmptyStr,
    role: NonEmptyStr,
    initials: maybe_(Str),
    email: maybe_(Str),
    level: maybe_(Str),
    tenure: maybe_(Str),
    pronouns: maybe_(Str),
    timezone: maybe_(Str),
    color: maybe_(Str),
    nextOneOnOne: maybe_(Str),
    growthFocus: maybe_(Str),
    growthProgress: maybe_(Num),
    tags: optional_(arrayOf_(Str)),
    flags: optional_(arrayOf_(Str)),
  })
);

// PATCH input — every field optional.
export const UpdatePersonInput = sdef(
  "UpdatePersonInput",
  shape_({
    name: optional_(NonEmptyStr),
    role: optional_(NonEmptyStr),
    initials: optional_(NonEmptyStr),
    email: maybe_(Str),
    level: maybe_(Str),
    tenure: maybe_(Str),
    pronouns: maybe_(Str),
    timezone: maybe_(Str),
    color: maybe_(Str),
    nextOneOnOne: maybe_(Str),
    lastNote: maybe_(Str),
    sentimentLabel: maybe_(Str),
    pto: maybe_(Str),
    growthFocus: maybe_(Str),
    growthProgress: maybe_(Num),
    tags: optional_(arrayOf_(Str)),
    flags: optional_(arrayOf_(Str)),
  })
);
