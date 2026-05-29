import { atf, assoc, createId } from "@em-notes/core";
import * as peopleRepo from "../repositories/people-repo.js";
import * as actionsRepo from "../repositories/actions-repo.js";

// Read: attach the cross-entity fields (sentiment trend, derived openActions) onto the base
// Person rows so API responses match the prototype Person shape.
export function listPeople(db) {
  const sentiment = peopleRepo.sentimentByPerson(db);
  const openCounts = actionsRepo.openCountByPerson(db);
  return peopleRepo.listPeople(db).map((p) => withDerived(p, sentiment, openCounts));
}

export function getPerson(db, id) {
  const person = peopleRepo.getPerson(db, id);
  if (!person) return null;
  return withDerived(person, peopleRepo.sentimentByPerson(db), actionsRepo.openCountByPerson(db));
}

function withDerived(person, sentiment, openCounts) {
  return assoc(person, {
    sentiment: sentiment[person.id] ?? [],
    openActions: openCounts[person.id] ?? 0,
  });
}

// Create: derive initials if absent, assign an id, persist, return the full read shape.
export function createPerson(db, input) {
  return atf(input, addInitials, addPersonId, (person) => {
    peopleRepo.insertPerson(db, person);
    return getPerson(db, person.id);
  });
}

function addInitials(input) {
  if (input.initials) return input;
  const initials = input.name
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((w) => w[0].toUpperCase())
    .join("");
  return assoc(input, { initials });
}

function addPersonId(person) {
  return assoc(person, { id: createId("person") });
}

export function updatePerson(db, id, patch) {
  if (!peopleRepo.getPerson(db, id)) return null;
  peopleRepo.updatePerson(db, id, patch);
  return getPerson(db, id);
}

export function deletePerson(db, id) {
  return peopleRepo.deletePerson(db, id);
}
