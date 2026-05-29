import { apiFetch } from "./client.js";

export const listActions = () => apiFetch("/actions");

export const createAction = (action) =>
  apiFetch("/actions", { method: "POST", body: JSON.stringify(action) });

export const updateAction = (id, patch) =>
  apiFetch(`/actions/${id}`, { method: "PATCH", body: JSON.stringify(patch) });

export const deleteAction = (id) => apiFetch(`/actions/${id}`, { method: "DELETE" });
