import { apiFetch } from "./client.js";

export const listNotes = (personId) => apiFetch(`/people/${personId}/notes`);
export const getNote = (id) => apiFetch(`/notes/${id}`);

export const createNote = (personId, note) =>
  apiFetch(`/people/${personId}/notes`, { method: "POST", body: JSON.stringify(note) });

export const updateNote = (id, patch) =>
  apiFetch(`/notes/${id}`, { method: "PATCH", body: JSON.stringify(patch) });

export const deleteNote = (id) => apiFetch(`/notes/${id}`, { method: "DELETE" });
