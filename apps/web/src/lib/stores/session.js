import { writable } from "svelte/store";
import * as authApi from "../api/auth-api.js";
import { resetPeople } from "./people.js";
import { resetActions } from "./actions.js";
import { resetNotes } from "./notes.js";
import { resetSettings } from "./settings.js";
import { resetCalendar } from "./calendar.js";

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
  // Drop in-memory domain data so nothing sensitive lingers after sign-out.
  resetPeople();
  resetActions();
  resetNotes();
  resetSettings();
  resetCalendar();
  await refreshSession();
}
