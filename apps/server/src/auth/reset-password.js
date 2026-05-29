import { loadConfig } from "../config.js";
import { openDb } from "../db.js";
import { deleteAllSessions } from "./sessions.js";

// Local password reset (§10): clears auth_user and sessions only. The next run requires
// setup again. CLI: `pnpm --filter @em-notes/server auth:reset`.
const config = loadConfig();
const db = openDb(config.dbPath);

const users = db.prepare("DELETE FROM auth_user").run();
deleteAllSessions(db);

console.log(
  `Cleared ${users.changes} user(s) and all sessions. Run setup again to set a new password.`
);
db.close();
