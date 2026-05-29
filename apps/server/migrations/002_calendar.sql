-- Calendar sync (feed-URL subscription) + person email for attendee matching.

-- Email lets us match calendar attendees to direct reports.
ALTER TABLE people ADD COLUMN email TEXT;

-- A subscribed .ics feed (Google/Outlook/Apple "secret iCal address"). Read-only.
CREATE TABLE calendar_feeds (
  id TEXT PRIMARY KEY,
  url TEXT NOT NULL,
  label TEXT NOT NULL,
  last_synced_at TEXT,
  last_error TEXT,
  created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Cached expanded event instances from the feeds (replaced wholesale per feed on each sync).
-- `person_id` is the matched report (by attendee email, then name), null if unmatched.
CREATE TABLE calendar_events (
  id TEXT PRIMARY KEY,
  feed_id TEXT NOT NULL REFERENCES calendar_feeds(id) ON DELETE CASCADE,
  uid TEXT,
  summary TEXT NOT NULL DEFAULT '',
  location TEXT,
  starts_at TEXT NOT NULL,
  ends_at TEXT,
  attendees_json TEXT NOT NULL DEFAULT '[]',
  person_id TEXT REFERENCES people(id) ON DELETE SET NULL,
  created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_calendar_events_starts_at ON calendar_events(starts_at);
CREATE INDEX idx_calendar_events_person ON calendar_events(person_id);
