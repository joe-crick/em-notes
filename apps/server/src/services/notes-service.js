import { atf, assoc, createId, map } from "@em-notes/core";
import * as notesRepo from "../repositories/notes-repo.js";
import * as actionsRepo from "../repositories/actions-repo.js";

// Read: compose each note with its action_items (cross-entity).
export function listNotesByPerson(db, personId) {
  return map((note) => withActions(db, note), notesRepo.listNotesByPerson(db, personId));
}

export function getNote(db, id) {
  const note = notesRepo.getNote(db, id);
  return note ? withActions(db, note) : null;
}

function withActions(db, note) {
  return assoc(note, { actions: actionsRepo.listActionsByNote(db, note.id) });
}

// Create: apply defaults, assign an id, persist the note + any embedded actions (each linked to
// the note and person) in one transaction, return the composed read shape. The contract makes
// `type`/`date` optional but the table requires them, so we default here (plan §1.1 keeps the API
// forgiving rather than 500-ing on a minimal body).
export function createNote(db, input) {
  return atf(input, addNoteDefaults, addNoteId, (note) => {
    // All-or-nothing: a failing embedded action must not leave a half-written note.
    db.transaction(() => {
      notesRepo.insertNote(db, note);
      persistEmbeddedActions(db, note);
    })();
    return getNote(db, note.id);
  });
}

function addNoteDefaults(input) {
  return assoc(input, {
    type: input.type ?? "1:1",
    date: input.date ?? todayLabel(),
  });
}

// Human date matching the seed convention ("May 29, 2026").
function todayLabel() {
  return new Intl.DateTimeFormat("en-US", {
    month: "long",
    day: "numeric",
    year: "numeric",
  }).format(new Date());
}

function addNoteId(input) {
  return assoc(input, { id: createId("note") });
}

function persistEmbeddedActions(db, note) {
  for (const action of note.actions ?? []) {
    actionsRepo.insertAction(
      db,
      assoc(action, {
        id: action.id ?? createId("action"),
        noteId: note.id,
        personId: note.personId,
      })
    );
  }
}

export function updateNote(db, id, patch) {
  if (!notesRepo.getNote(db, id)) return null;
  notesRepo.updateNote(db, id, patch);
  return getNote(db, id);
}

export function deleteNote(db, id) {
  return notesRepo.deleteNote(db, id);
}
