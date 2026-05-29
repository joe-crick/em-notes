import { apiFetch } from "./client.js";

export const getStatus = () => apiFetch("/auth/status");

export const setupPassword = (password) =>
  apiFetch("/auth/setup", { method: "POST", body: JSON.stringify({ password }) });

export const login = (password) =>
  apiFetch("/auth/login", { method: "POST", body: JSON.stringify({ password }) });

export const logout = () => apiFetch("/auth/logout", { method: "POST" });
