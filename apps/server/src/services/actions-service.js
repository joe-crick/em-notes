import { atf, assoc, createId } from "@em-notes/core";
import * as actionsRepo from "../repositories/actions-repo.js";
import * as peopleRepo from "../repositories/people-repo.js";
import * as notesRepo from "../repositories/notes-repo.js";

export function listActions(db) {
  return actionsRepo.listActions(db);
}

// Guard the foreign keys before we hand them to SQLite, so a bad personId/noteId becomes a
// structured 400 instead of a constraint-violation 500 (the DB has FKs on both columns).
function referenceError(db, { personId, noteId }) {
  if (personId != null && !peopleRepo.getPerson(db, personId)) {
    return { code: "invalid_reference", message: `Unknown personId: ${personId}` };
  }
  if (noteId != null && !notesRepo.getNote(db, noteId)) {
    return { code: "invalid_reference", message: `Unknown noteId: ${noteId}` };
  }
  return null;
}

// Returns { ok: true, data } | { ok: false, error }.
export async function createAction(db, input) {
  const error = referenceError(db, input);
  if (error) return { ok: false, error };
  const data = await atf(input, addActionId, (action) => {
    actionsRepo.insertAction(db, action);
    return actionsRepo.getAction(db, action.id);
  });
  return { ok: true, data };
}

function addActionId(input) {
  return assoc(input, { id: createId("action") });
}

// Returns { ok: true, data } | { ok: false, error } | { ok: false, notFound: true }.
export function updateAction(db, id, patch) {
  if (!actionsRepo.getAction(db, id)) return { ok: false, notFound: true };
  const error = referenceError(db, patch);
  if (error) return { ok: false, error };
  return { ok: true, data: actionsRepo.updateAction(db, id, patch) };
}

export function deleteAction(db, id) {
  return actionsRepo.deleteAction(db, id);
}
