import { writable } from "svelte/store";
import * as notesApi from "../api/notes-api.js";

// Notes for the currently-viewed person. The Person screen subscribes; the New Note modal
// reloads this after a save so the list/count refresh without a navigation.
export const notes = writable([]);
export const notesPersonId = writable(null);

export async function loadNotes(personId) {
  notesPersonId.set(personId);
  const res = await notesApi.listNotes(personId);
  if (res.ok) notes.set(res.data);
  return res;
}
