import { writable } from "svelte/store";
import * as notesApi from "../api/notes-api.js";

// Notes for the currently-viewed person. The Person screen subscribes; the New Note modal
// reloads this after a save so the list/count refresh without a navigation.
export const notes = writable([]);
export const notesPersonId = writable(null);

// Guards against an out-of-order race: if the user switches people quickly, only the most recent
// request is allowed to write the store (an earlier, slower response is discarded).
let requestToken = 0;

export async function loadNotes(personId) {
  const token = ++requestToken;
  notesPersonId.set(personId);
  const res = await notesApi.listNotes(personId);
  if (token !== requestToken) return res; // a newer load superseded this one
  if (res.ok) notes.set(res.data);
  return res;
}

export function resetNotes() {
  requestToken += 1;
  notes.set([]);
  notesPersonId.set(null);
}
