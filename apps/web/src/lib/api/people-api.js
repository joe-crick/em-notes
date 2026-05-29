import { apiFetch } from "./client.js";

export const listPeople = () => apiFetch("/people");
export const getPerson = (id) => apiFetch(`/people/${id}`);

export const createPerson = (input) =>
  apiFetch("/people", { method: "POST", body: JSON.stringify(input) });

export const updatePerson = (id, patch) =>
  apiFetch(`/people/${id}`, { method: "PATCH", body: JSON.stringify(patch) });

export const deletePerson = (id) => apiFetch(`/people/${id}`, { method: "DELETE" });
