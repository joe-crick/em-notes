import { writable } from "svelte/store";
import * as authApi from "../api/auth-api.js";

// status: "loading" until the first /auth/status resolves, then "ready".
export const session = writable({ status: "loading", configured: false, authenticated: false });

export async function refreshSession() {
  const res = await authApi.getStatus();
  if (res.ok) session.set({ status: "ready", ...res.data });
  return res;
}

export async function setupPassword(password) {
  const res = await authApi.setupPassword(password);
  if (res.ok) session.set({ status: "ready", configured: true, authenticated: true });
  return res;
}

export async function login(password) {
  const res = await authApi.login(password);
  if (res.ok) await refreshSession();
  return res;
}

export async function logout() {
  await authApi.logout();
  await refreshSession();
}
