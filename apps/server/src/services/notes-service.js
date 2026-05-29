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

// Create: assign an id, persist the note + any embedded actions (each linked to the note and
// person), return the composed read shape.
export function createNote(db, input) {
  return atf(input, addNoteId, (note) => {
    notesRepo.insertNote(db, note);
    persistEmbeddedActions(db, note);
    return getNote(db, note.id);
  });
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
