import { resolve } from "node:path";

// Runtime configuration from environment variables (implementation-plan §14), with
// local-first defaults. The server binds to 127.0.0.1 by default (hard constraint §2.6).
const DEFAULTS = {
  EM_NOTES_DB_PATH: "./data/em-notes.sqlite",
  EM_NOTES_HOST: "127.0.0.1",
  EM_NOTES_PORT: "5174",
  EM_NOTES_WEB_ORIGIN: "http://127.0.0.1:5173",
  EM_NOTES_SESSION_DAYS: "14",
};

export function loadConfig(env = process.env) {
  const get = (key) => env[key] ?? DEFAULTS[key];
  return {
    dbPath: resolve(process.cwd(), get("EM_NOTES_DB_PATH")),
    host: get("EM_NOTES_HOST"),
    port: Number(get("EM_NOTES_PORT")),
    webOrigin: get("EM_NOTES_WEB_ORIGIN"),
    sessionDays: Number(get("EM_NOTES_SESSION_DAYS")),
  };
}
