import { hashPassword, verifyPassword } from "./password.js";
import { createSession, getValidSession, deleteSession } from "./sessions.js";

// Single-user local auth (§10): one row in auth_user, fixed id.
const USER_ID = "local";

// Throttle brute-force guessing with an escalating delay on consecutive failures (reset on
// success). Local-only app, so an in-memory counter is sufficient; capped so a forgetful user
// isn't locked out for long.
let failedAttempts = 0;
const MAX_FAILED_DELAY_MS = 2000;
const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

export function isConfigured(db) {
  return !!db.prepare("SELECT 1 FROM auth_user LIMIT 1").get();
}

function getUser(db) {
  return db
    .prepare("SELECT id, password_hash AS passwordHash FROM auth_user WHERE id = ?")
    .get(USER_ID);
}

export function getStatus(db, sessionId) {
  return {
    configured: isConfigured(db),
    authenticated: !!getValidSession(db, sessionId),
  };
}

// First-run setup: store the Argon2id hash and sign the user in. Refuses if already set.
export async function setupPassword(db, password, sessionDays) {
  if (isConfigured(db)) {
    return { ok: false, error: { code: "already_configured", message: "Password already set." } };
  }
  const passwordHash = await hashPassword(password);
  db.prepare("INSERT INTO auth_user (id, password_hash) VALUES (?, ?)").run(USER_ID, passwordHash);
  return { ok: true, session: createSession(db, USER_ID, sessionDays) };
}

export async function login(db, password, sessionDays) {
  const user = getUser(db);
  if (!user) {
    return { ok: false, error: { code: "not_configured", message: "No password configured." } };
  }
  if (!(await verifyPassword(user.passwordHash, password))) {
    failedAttempts += 1;
    await sleep(Math.min(failedAttempts * 250, MAX_FAILED_DELAY_MS));
    return { ok: false, error: { code: "invalid_credentials", message: "Incorrect password." } };
  }
  failedAttempts = 0;
  return { ok: true, session: createSession(db, user.id, sessionDays) };
}

export function logout(db, sessionId) {
  if (sessionId) deleteSession(db, sessionId);
  return { ok: true };
}
