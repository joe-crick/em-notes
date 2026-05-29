import { atf, assoc, createId } from "@em-notes/core";
import * as actionsRepo from "../repositories/actions-repo.js";

export function listActions(db) {
  return actionsRepo.listActions(db);
}

export function createAction(db, input) {
  return atf(input, addActionId, (action) => {
    actionsRepo.insertAction(db, action);
    return actionsRepo.getAction(db, action.id);
  });
}

function addActionId(input) {
  return assoc(input, { id: createId("action") });
}

export function updateAction(db, id, patch) {
  if (!actionsRepo.getAction(db, id)) return null;
  return actionsRepo.updateAction(db, id, patch);
}

export function deleteAction(db, id) {
  return actionsRepo.deleteAction(db, id);
}
