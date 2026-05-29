// people-repo — SQL only for the `people` and `sentiment_points` tables, plus the
// row<->domain (snake_case <-> camelCase) mapping (§14 layering, §15.1).
const n = (v) => v ?? null;

// camelCase domain field -> people column, for the scalar (non-JSON) fields.
const SCALAR_COLUMNS = {
  name: "name",
  role: "role",
  email: "email",
  level: "level",
  tenure: "tenure",
  pronouns: "pronouns",
  timezone: "timezone",
  initials: "initials",
  color: "color",
  nextOneOnOne: "next_one_on_one",
  lastNote: "last_note_at",
  sentimentLabel: "sentiment_label",
  pto: "pto",
  growthFocus: "growth_focus",
  growthProgress: "growth_progress",
};

// Map a people row to the base Person shape (without cross-entity sentiment / openActions,
// which the service attaches).
function mapPersonRow(row) {
  return {
    id: row.id,
    name: row.name,
    role: row.role,
    email: row.email,
    level: row.level,
    tenure: row.tenure,
    pronouns: row.pronouns,
    timezone: row.timezone,
    initials: row.initials,
    color: row.color,
    nextOneOnOne: row.next_one_on_one,
    lastNote: row.last_note_at,
    sentimentLabel: row.sentiment_label,
    pto: row.pto,
    growthFocus: row.growth_focus,
    growthProgress: row.growth_progress,
    flags: JSON.parse(row.flags_json),
    tags: JSON.parse(row.tags_json),
  };
}

export function insertPerson(db, person) {
  return db
    .prepare(
      `INSERT OR IGNORE INTO people
         (id, name, role, email, level, tenure, pronouns, timezone, initials, color,
          next_one_on_one, last_note_at, sentiment_label, pto, growth_focus,
          growth_progress, tags_json, flags_json)
       VALUES
         (@id, @name, @role, @email, @level, @tenure, @pronouns, @timezone, @initials, @color,
          @next_one_on_one, @last_note_at, @sentiment_label, @pto, @growth_focus,
          @growth_progress, @tags_json, @flags_json)`
    )
    .run({
      id: person.id,
      name: person.name,
      role: person.role,
      email: n(person.email),
      level: n(person.level),
      tenure: n(person.tenure),
      pronouns: n(person.pronouns),
      timezone: n(person.timezone),
      initials: person.initials,
      color: n(person.color),
      next_one_on_one: n(person.nextOneOnOne),
      last_note_at: n(person.lastNote),
      sentiment_label: n(person.sentimentLabel),
      pto: n(person.pto),
      growth_focus: n(person.growthFocus),
      growth_progress: n(person.growthProgress),
      tags_json: JSON.stringify(person.tags ?? []),
      flags_json: JSON.stringify(person.flags ?? []),
    });
}

export function listPeople(db) {
  return db.prepare("SELECT * FROM people ORDER BY rowid").all().map(mapPersonRow);
}

export function getPerson(db, id) {
  const row = db.prepare("SELECT * FROM people WHERE id = ?").get(id);
  return row ? mapPersonRow(row) : null;
}

export function updatePerson(db, id, patch) {
  const sets = [];
  const params = { id };

  // `conform` fills every optional shape key with `undefined`, so test for a defined value
  // rather than key presence. An explicit `null` is a valid "clear" and is kept.
  for (const [field, column] of Object.entries(SCALAR_COLUMNS)) {
    if (patch[field] !== undefined) {
      sets.push(`${column} = @${column}`);
      params[column] = n(patch[field]);
    }
  }
  if (patch.tags !== undefined) {
    sets.push("tags_json = @tags_json");
    params.tags_json = JSON.stringify(patch.tags ?? []);
  }
  if (patch.flags !== undefined) {
    sets.push("flags_json = @flags_json");
    params.flags_json = JSON.stringify(patch.flags ?? []);
  }
  if (sets.length === 0) return getPerson(db, id);

  sets.push("updated_at = CURRENT_TIMESTAMP");
  const info = db.prepare(`UPDATE people SET ${sets.join(", ")} WHERE id = @id`).run(params);
  return info.changes ? getPerson(db, id) : null;
}

export function deletePerson(db, id) {
  return db.prepare("DELETE FROM people WHERE id = ?").run(id).changes > 0;
}

// --- sentiment_points (closely tied to people) ---

export function insertSentimentPoint(db, point) {
  return db
    .prepare(
      `INSERT OR IGNORE INTO sentiment_points (id, person_id, score, recorded_at)
       VALUES (@id, @person_id, @score, @recorded_at)`
    )
    .run({
      id: point.id,
      person_id: point.personId,
      score: point.score,
      recorded_at: point.recordedAt,
    });
}

// { personId: [score, ...] } ordered oldest-first, for the sentiment sparkline.
export function sentimentByPerson(db) {
  const rows = db
    .prepare("SELECT person_id, score FROM sentiment_points ORDER BY recorded_at")
    .all();
  const byPerson = {};
  for (const row of rows) {
    (byPerson[row.person_id] ??= []).push(row.score);
  }
  return byPerson;
}
