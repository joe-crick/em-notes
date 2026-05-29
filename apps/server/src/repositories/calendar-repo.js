// calendar-repo — SQL only for `calendar_feeds` + `calendar_events`.
const n = (v) => v ?? null;

function mapFeedRow(row) {
  return {
    id: row.id,
    url: row.url,
    label: row.label,
    lastSyncedAt: row.last_synced_at,
    lastError: row.last_error,
  };
}

export function insertFeed(db, feed) {
  db.prepare("INSERT INTO calendar_feeds (id, url, label) VALUES (@id, @url, @label)").run({
    id: feed.id,
    url: feed.url,
    label: feed.label,
  });
  return getFeed(db, feed.id);
}

export function listFeeds(db) {
  return db.prepare("SELECT * FROM calendar_feeds ORDER BY rowid").all().map(mapFeedRow);
}

export function getFeed(db, id) {
  const row = db.prepare("SELECT * FROM calendar_feeds WHERE id = ?").get(id);
  return row ? mapFeedRow(row) : null;
}

export function deleteFeed(db, id) {
  return db.prepare("DELETE FROM calendar_feeds WHERE id = ?").run(id).changes > 0;
}

export function setFeedStatus(db, id, { lastSyncedAt, lastError }) {
  db.prepare(
    `UPDATE calendar_feeds
       SET last_synced_at = @last_synced_at, last_error = @last_error, updated_at = CURRENT_TIMESTAMP
     WHERE id = @id`
  ).run({ id, last_synced_at: n(lastSyncedAt), last_error: n(lastError) });
}

// --- events ---

function mapEventRow(row) {
  return {
    id: row.id,
    summary: row.summary,
    location: row.location,
    startsAt: row.starts_at,
    endsAt: row.ends_at,
    attendees: JSON.parse(row.attendees_json),
    personId: row.person_id,
  };
}

// Replace all cached events for one feed in a single transaction.
export function replaceFeedEvents(db, feedId, events) {
  const del = db.prepare("DELETE FROM calendar_events WHERE feed_id = ?");
  const ins = db.prepare(
    `INSERT INTO calendar_events
       (id, feed_id, uid, summary, location, starts_at, ends_at, attendees_json, person_id)
     VALUES
       (@id, @feed_id, @uid, @summary, @location, @starts_at, @ends_at, @attendees_json, @person_id)`
  );
  db.transaction(() => {
    del.run(feedId);
    for (const e of events) {
      ins.run({
        id: e.id,
        feed_id: feedId,
        uid: n(e.uid),
        summary: e.summary ?? "",
        location: n(e.location),
        starts_at: e.startsAt,
        ends_at: n(e.endsAt),
        attendees_json: JSON.stringify(e.attendees ?? []),
        person_id: n(e.personId),
      });
    }
  })();
}

export function listEventsBetween(db, fromIso, toIso) {
  return db
    .prepare(
      "SELECT * FROM calendar_events WHERE starts_at >= ? AND starts_at <= ? ORDER BY starts_at"
    )
    .all(fromIso, toIso)
    .map(mapEventRow);
}
