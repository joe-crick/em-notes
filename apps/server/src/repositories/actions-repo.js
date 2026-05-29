// actions-repo — SQL only for the `action_items` table + row<->domain mapping.
const n = (v) => v ?? null;

const SCALAR_COLUMNS = {
  text: "text",
  owner: "owner",
  personId: "person_id",
  noteId: "note_id",
  dueAt: "due_at",
};

function mapActionRow(row) {
  return {
    id: row.id,
    personId: row.person_id,
    noteId: row.note_id,
    text: row.text,
    owner: row.owner,
    done: !!row.done,
    dueAt: row.due_at,
  };
}

export function insertAction(db, action) {
  return db
    .prepare(
      `INSERT OR IGNORE INTO action_items
         (id, person_id, note_id, text, owner, done, due_at)
       VALUES
         (@id, @person_id, @note_id, @text, @owner, @done, @due_at)`
    )
    .run({
      id: action.id,
      person_id: n(action.personId),
      note_id: n(action.noteId),
      text: action.text,
      owner: action.owner ?? "me",
      done: action.done ? 1 : 0,
      due_at: n(action.dueAt),
    });
}

export function listActions(db) {
  return db.prepare("SELECT * FROM action_items ORDER BY done, rowid").all().map(mapActionRow);
}

export function listActionsByNote(db, noteId) {
  return db
    .prepare("SELECT * FROM action_items WHERE note_id = ? ORDER BY rowid")
    .all(noteId)
    .map(mapActionRow);
}

export function getAction(db, id) {
  const row = db.prepare("SELECT * FROM action_items WHERE id = ?").get(id);
  return row ? mapActionRow(row) : null;
}

export function updateAction(db, id, patch) {
  const sets = [];
  const params = { id };

  // `conform` fills every optional shape key with `undefined`; test for a defined value.
  for (const [field, column] of Object.entries(SCALAR_COLUMNS)) {
    if (patch[field] !== undefined) {
      sets.push(`${column} = @${column}`);
      params[column] = n(patch[field]);
    }
  }
  if (patch.done !== undefined) {
    sets.push("done = @done");
    params.done = patch.done ? 1 : 0;
  }
  if (sets.length === 0) return getAction(db, id);

  sets.push("updated_at = CURRENT_TIMESTAMP");
  const info = db.prepare(`UPDATE action_items SET ${sets.join(", ")} WHERE id = @id`).run(params);
  return info.changes ? getAction(db, id) : null;
}

export function deleteAction(db, id) {
  return db.prepare("DELETE FROM action_items WHERE id = ?").run(id).changes > 0;
}

// { personId: openCount } for action_items that aren't done (derives Person.openActions, §15.1).
export function openCountByPerson(db) {
  const rows = db
    .prepare(
      "SELECT person_id, COUNT(*) AS c FROM action_items WHERE done = 0 AND person_id IS NOT NULL GROUP BY person_id"
    )
    .all();
  return Object.fromEntries(rows.map((r) => [r.person_id, r.c]));
}
