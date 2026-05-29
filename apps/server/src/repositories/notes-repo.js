// notes-repo — SQL only for the `notes` table + row<->domain mapping (§15.2). Embedded
// `actions` are composed by the service (cross-entity), not here.
const n = (v) => v ?? null;

const SCALAR_COLUMNS = {
  date: "note_date",
  type: "type",
  duration: "duration",
  sentiment: "sentiment",
  summary: "summary",
};

function mapNoteRow(row) {
  return {
    id: row.id,
    personId: row.person_id,
    date: row.note_date,
    type: row.type,
    duration: row.duration,
    sentiment: row.sentiment,
    summary: row.summary,
    highlights: JSON.parse(row.highlights_json),
    transcript: !!row.transcript,
  };
}

export function insertNote(db, note) {
  return db
    .prepare(
      `INSERT OR IGNORE INTO notes
         (id, person_id, note_date, type, duration, sentiment, summary, highlights_json, transcript)
       VALUES
         (@id, @person_id, @note_date, @type, @duration, @sentiment, @summary, @highlights_json, @transcript)`
    )
    .run({
      id: note.id,
      person_id: note.personId,
      note_date: note.date,
      type: note.type,
      duration: n(note.duration),
      sentiment: n(note.sentiment),
      summary: note.summary,
      highlights_json: JSON.stringify(note.highlights ?? []),
      transcript: note.transcript ? 1 : 0,
    });
}

export function listNotesByPerson(db, personId) {
  // Newest-first. Notes seeded together share a created_at, so rowid breaks ties in insertion
  // order (the seed inserts newest-first); a note created later sorts ahead of all of them.
  return db
    .prepare("SELECT * FROM notes WHERE person_id = ? ORDER BY created_at DESC, rowid")
    .all(personId)
    .map(mapNoteRow);
}

export function getNote(db, id) {
  const row = db.prepare("SELECT * FROM notes WHERE id = ?").get(id);
  return row ? mapNoteRow(row) : null;
}

export function updateNote(db, id, patch) {
  const sets = [];
  const params = { id };

  // `conform` fills every optional shape key with `undefined`; test for a defined value.
  for (const [field, column] of Object.entries(SCALAR_COLUMNS)) {
    if (patch[field] !== undefined) {
      sets.push(`${column} = @${column}`);
      params[column] = n(patch[field]);
    }
  }
  if (patch.highlights !== undefined) {
    sets.push("highlights_json = @highlights_json");
    params.highlights_json = JSON.stringify(patch.highlights ?? []);
  }
  if (patch.transcript !== undefined) {
    sets.push("transcript = @transcript");
    params.transcript = patch.transcript ? 1 : 0;
  }
  if (sets.length === 0) return getNote(db, id);

  sets.push("updated_at = CURRENT_TIMESTAMP");
  const info = db.prepare(`UPDATE notes SET ${sets.join(", ")} WHERE id = @id`).run(params);
  return info.changes ? getNote(db, id) : null;
}

export function deleteNote(db, id) {
  return db.prepare("DELETE FROM notes WHERE id = ?").run(id).changes > 0;
}
