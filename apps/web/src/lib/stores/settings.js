import { writable } from "svelte/store";
import * as settingsApi from "../api/settings-api.js";
import { ME } from "../manager.js";

// User settings (theme, density, team name). Theme/density mirror the `data-theme` /
// `data-density` body attributes the prototype CSS keys off, so changing one reflows the app.
const DEFAULTS = { theme: "light", density: "comfortable", teamName: ME.team };

function applyToBody({ theme, density }) {
  if (typeof document === "undefined") return;
  if (theme) document.body.dataset.theme = theme;
  if (density) document.body.dataset.density = density;
}

export const settings = writable({ ...DEFAULTS });

export async function loadSettings() {
  const res = await settingsApi.getSettings();
  if (res.ok) {
    settings.set({ ...DEFAULTS, ...res.data });
    applyToBody(res.data);
  }
  return res;
}

export function resetSettings() {
  settings.set({ ...DEFAULTS });
}

export async function updateSettings(patch) {
  const res = await settingsApi.updateSettings(patch);
  if (res.ok) {
    settings.set({ ...DEFAULTS, ...res.data });
    applyToBody(res.data);
  }
  return res;
}
