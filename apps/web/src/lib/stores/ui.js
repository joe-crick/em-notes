import { writable } from "svelte/store";

// Transient UI state: which overlay (if any) is open. The New Note modal carries the person
// it's being created for; the others are simple booleans.
export const newNotePerson = writable(null);
export const addReportOpen = writable(false);
export const editPerson = writable(null);
export const deletePersonTarget = writable(null);
export const paletteOpen = writable(false);

export const openNewNote = (person) => newNotePerson.set(person);
export const closeNewNote = () => newNotePerson.set(null);
export const openAddReport = () => addReportOpen.set(true);
export const closeAddReport = () => addReportOpen.set(false);
export const openEditPerson = (person) => editPerson.set(person);
export const closeEditPerson = () => editPerson.set(null);
export const openDeletePerson = (person) => deletePersonTarget.set(person);
export const closeDeletePerson = () => deletePersonTarget.set(null);
export const openPalette = () => paletteOpen.set(true);
export const closePalette = () => paletteOpen.set(false);
export const togglePalette = () => paletteOpen.update((v) => !v);
