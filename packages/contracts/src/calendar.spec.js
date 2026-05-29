import { sdef, shape_, arrayOf_, NonEmptyStr, Str } from "ljspec";
import { maybe_, optional_ } from "./helpers.js";

// A subscribed read-only .ics calendar feed (Google/Outlook/Apple "secret iCal address").
export const CalendarFeed = sdef(
  "CalendarFeed",
  shape_({
    id: NonEmptyStr,
    url: NonEmptyStr,
    label: NonEmptyStr,
    lastSyncedAt: maybe_(Str),
    lastError: maybe_(Str),
  })
);

// Add-feed input. Label is optional (defaults to the URL host server-side).
export const CreateCalendarFeedInput = sdef(
  "CreateCalendarFeedInput",
  shape_({
    url: NonEmptyStr,
    label: optional_(NonEmptyStr),
  })
);

// An expanded event instance surfaced to the UI (Home agenda). `personId` is the matched
// report, if any.
export const CalendarEvent = sdef(
  "CalendarEvent",
  shape_({
    id: NonEmptyStr,
    summary: Str,
    location: maybe_(Str),
    startsAt: NonEmptyStr,
    endsAt: maybe_(Str),
    attendees: optional_(arrayOf_(Str)),
    personId: maybe_(NonEmptyStr),
  })
);
