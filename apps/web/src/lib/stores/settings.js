import { writable } from "svelte/store";
import * as settingsApi from "../api/settings-api.js";

// User settings (theme + density). Mirrors the `data-theme` / `data-density` body attributes
// the prototype CSS keys off, so changing a setting reflows the whole app.
export const settings = writable({ theme: "light", density: "comfortable" });

function applyToBody({ theme, density }) {
  if (typeof document === "undefined") return;
  if (theme) document.body.dataset.theme = theme;
  if (density) document.body.dataset.density = density;
}

export async function loadSettings() {
  const res = await settingsApi.getSettings();
  if (res.ok) {
    settings.set({ theme: "light", density: "comfortable", ...res.data });
    applyToBody(res.data);
  }
  return res;
}

export async function updateSettings(patch) {
  const res = await settingsApi.updateSettings(patch);
  if (res.ok) {
    settings.set({ theme: "light", density: "comfortable", ...res.data });
    applyToBody(res.data);
  }
  return res;
}
