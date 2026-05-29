import { createId } from "@em-notes/core";

// Server-side sessions stored in SQLite (§10). SQL only.
const DAY_MS = 24 * 60 * 60 * 1000;

export function createSession(db, userId, sessionDays) {
  const id = createId("sess");
  const expiresAt = new Date(Date.now() + sessionDays * DAY_MS).toISOString();
  db.prepare("INSERT INTO sessions (id, user_id, expires_at) VALUES (?, ?, ?)").run(
    id,
    userId,
    expiresAt
  );
  return { id, userId, expiresAt };
}

// Returns the session row if it exists and hasn't expired; expired rows are purged and
// treated as absent.
export function getValidSession(db, sessionId) {
  if (!sessionId) return null;
  const row = db
    .prepare("SELECT id, user_id AS userId, expires_at AS expiresAt FROM sessions WHERE id = ?")
    .get(sessionId);
  if (!row) return null;
  if (new Date(row.expiresAt).getTime() <= Date.now()) {
    deleteSession(db, sessionId);
    return null;
  }
  return row;
}

export function deleteSession(db, sessionId) {
  db.prepare("DELETE FROM sessions WHERE id = ?").run(sessionId);
}

export function deleteAllSessions(db) {
  db.prepare("DELETE FROM sessions").run();
}
