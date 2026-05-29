import { UserSettings, isValid, explain } from "@em-notes/contracts";
import * as settingsRepo from "../repositories/settings-repo.js";

export function getSettings(db) {
  return settingsRepo.getSettings(db);
}

// Merge the patch over the stored settings, then validate the *merged* result against
// UserSettings before persisting — so a partial patch can't drive the stored settings into an
// invalid combined state. Returns { ok: true, data } | { ok: false, error }.
export function updateSettings(db, patch) {
  // `conform` fills absent optional fields with `undefined`; dropping them keeps a partial PATCH
  // from wiping settings it didn't mention (see docs/deviations.md, ljspec-conform gotcha).
  const clean = Object.fromEntries(Object.entries(patch).filter(([, v]) => v !== undefined));
  const merged = { ...settingsRepo.getSettings(db), ...clean };
  if (!isValid(UserSettings, merged)) {
    return {
      ok: false,
      error: { code: "invalid_request", message: explain(UserSettings, merged) },
    };
  }
  return { ok: true, data: settingsRepo.saveSettings(db, merged) };
}
