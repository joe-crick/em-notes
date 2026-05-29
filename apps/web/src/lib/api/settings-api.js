import { apiFetch } from "./client.js";

export const getSettings = () => apiFetch("/settings");

export const updateSettings = (patch) =>
  apiFetch("/settings", { method: "PATCH", body: JSON.stringify(patch) });
