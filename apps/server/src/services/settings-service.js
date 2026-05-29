import * as settingsRepo from "../repositories/settings-repo.js";

export function getSettings(db) {
  return settingsRepo.getSettings(db);
}

// Merge the patch over the stored settings and persist. The merged object is validated by
// UserSettings at the route boundary before this runs.
export function updateSettings(db, patch) {
  const merged = { ...settingsRepo.getSettings(db), ...patch };
  return settingsRepo.saveSettings(db, merged);
}
