import { writable } from "svelte/store";

// Transient UI state: which overlay (if any) is open. The New Note modal carries the person
// it's being created for; the others are simple booleans.
export const newNotePerson = writable(null);
export const addReportOpen = writable(false);
export const paletteOpen = writable(false);

export const openNewNote = (person) => newNotePerson.set(person);
export const closeNewNote = () => newNotePerson.set(null);
export const openAddReport = () => addReportOpen.set(true);
export const closeAddReport = () => addReportOpen.set(false);
