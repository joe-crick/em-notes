import { assoc, map, entries } from "@em-notes/core";

// Transform the prototype's `EM` object into normalized seed records that conform to
// @em-notes/contracts. This is the intended home for the small relational normalization
// (the prototype keys notes by person and uses `person`/`dueDate` on actions).

// People are already Person-shaped in the prototype; pass them through.
export function buildPeople(em) {
  return em.TEAM;
}

// Flatten NOTES `{ personId: [note, ...] }` into a flat Note[]: attach `personId` to each
// note and `personId`/`noteId` to each embedded action.
export function buildNotes(em) {
  return entries(em.NOTES).flatMap(([personId, notes]) =>
    map((note) => normalizeNote(note, personId), notes)
  );
}

function normalizeNote(note, personId) {
  const actions = map((a) => assoc(a, { personId, noteId: note.id }), note.actions || []);
  return assoc(note, { personId, actions });
}

// action_items seed comes from the prototype OPEN_ACTIONS list (it drives the Actions and
// Home screens). Normalize `person` -> `personId` and `dueDate` -> `dueAt`; display-only
// extras (due, from, urgent, overdue) ride along.
export function buildActions(em) {
  return map(normalizeAction, em.OPEN_ACTIONS);
}

function normalizeAction(a) {
  return assoc(a, { personId: a.person ?? null, dueAt: a.dueDate ?? null });
}

// The full seed payload.
export function buildSeed(em) {
  return {
    people: buildPeople(em),
    notes: buildNotes(em),
    actions: buildActions(em),
  };
}
