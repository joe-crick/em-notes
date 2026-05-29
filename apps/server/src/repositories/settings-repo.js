// settings-repo — SQL only. The whole UserSettings object is stored as one JSON row.
const SETTINGS_KEY = "user_settings";

export function getSettings(db) {
  const row = db.prepare("SELECT value_json FROM settings WHERE key = ?").get(SETTINGS_KEY);
  return row ? JSON.parse(row.value_json) : {};
}

export function saveSettings(db, settings) {
  db.prepare(
    `INSERT INTO settings (key, value_json) VALUES (@key, @value_json)
     ON CONFLICT(key) DO UPDATE SET value_json = @value_json, updated_at = CURRENT_TIMESTAMP`
  ).run({ key: SETTINGS_KEY, value_json: JSON.stringify(settings) });
  return settings;
}
