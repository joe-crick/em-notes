import { describe, it, expect } from "vitest";
import { openDb } from "../db.js";
import { migrate } from "../migrations.js";
import { createApp } from "../app.js";
import { SESSION_COOKIE } from "../auth/auth-routes.js";

const PASSWORD = "correct-horse-battery";

function makeApp() {
  const db = openDb(":memory:");
  migrate(db);
  const app = createApp({ db, config: { sessionDays: 14 }, logger: false });
  return { app, db };
}

// Pull the session cookie out of a Set-Cookie response so it can be replayed.
function sessionCookie(res) {
  const c = res.cookies.find((c) => c.name === SESSION_COOKIE);
  return c ? `${c.name}=${c.value}` : "";
}

describe("auth", () => {
  it("reports configured=false on a fresh DB", async () => {
    const { app } = makeApp();
    const res = await app.inject({ method: "GET", url: "/api/auth/status" });
    expect(res.json()).toEqual({ ok: true, data: { configured: false, authenticated: false } });
    await app.close();
  });

  it("rejects an invalid setup password payload", async () => {
    const { app } = makeApp();
    const res = await app.inject({
      method: "POST",
      url: "/api/auth/setup",
      payload: { password: "short" },
    });
    expect(res.statusCode).toBe(400);
    expect(res.json().ok).toBe(false);
    expect(res.json().error.code).toBe("invalid_request");
    await app.close();
  });

  it("stores an Argon2id hash, not the plaintext password", async () => {
    const { app, db } = makeApp();
    await app.inject({ method: "POST", url: "/api/auth/setup", payload: { password: PASSWORD } });
    const row = db.prepare("SELECT password_hash FROM auth_user WHERE id = 'local'").get();
    expect(row.password_hash).toMatch(/^\$argon2id\$/);
    expect(row.password_hash).not.toContain(PASSWORD);
    await app.close();
  });

  it("signs in on setup and sets an HttpOnly cookie", async () => {
    const { app } = makeApp();
    const res = await app.inject({
      method: "POST",
      url: "/api/auth/setup",
      payload: { password: PASSWORD },
    });
    expect(res.statusCode).toBe(200);
    const cookie = res.cookies.find((c) => c.name === SESSION_COOKIE);
    expect(cookie).toBeTruthy();
    expect(cookie.httpOnly).toBe(true);
    expect(cookie.sameSite).toBe("Strict");
    await app.close();
  });

  it("refuses setup when already configured", async () => {
    const { app } = makeApp();
    await app.inject({ method: "POST", url: "/api/auth/setup", payload: { password: PASSWORD } });
    const res = await app.inject({
      method: "POST",
      url: "/api/auth/setup",
      payload: { password: PASSWORD },
    });
    expect(res.statusCode).toBe(409);
    expect(res.json().error.code).toBe("already_configured");
    await app.close();
  });

  it("rejects login with the wrong password", async () => {
    const { app } = makeApp();
    await app.inject({ method: "POST", url: "/api/auth/setup", payload: { password: PASSWORD } });
    const res = await app.inject({
      method: "POST",
      url: "/api/auth/login",
      payload: { password: "nope-nope-nope" },
    });
    expect(res.statusCode).toBe(401);
    expect(res.json().error.code).toBe("invalid_credentials");
    await app.close();
  });

  it("accepts login with the correct password and sets a cookie", async () => {
    const { app } = makeApp();
    await app.inject({ method: "POST", url: "/api/auth/setup", payload: { password: PASSWORD } });
    const res = await app.inject({
      method: "POST",
      url: "/api/auth/login",
      payload: { password: PASSWORD },
    });
    expect(res.statusCode).toBe(200);
    expect(res.json()).toEqual({ ok: true, data: { authenticated: true } });
    expect(sessionCookie(res)).toContain(`${SESSION_COOKIE}=`);
    await app.close();
  });

  it("reports authenticated=true when presenting a valid session cookie", async () => {
    const { app } = makeApp();
    const setup = await app.inject({
      method: "POST",
      url: "/api/auth/setup",
      payload: { password: PASSWORD },
    });
    const res = await app.inject({
      method: "GET",
      url: "/api/auth/status",
      headers: { cookie: sessionCookie(setup) },
    });
    expect(res.json().data.authenticated).toBe(true);
    await app.close();
  });

  it("invalidates the session on logout", async () => {
    const { app, db } = makeApp();
    const setup = await app.inject({
      method: "POST",
      url: "/api/auth/setup",
      payload: { password: PASSWORD },
    });
    const cookie = sessionCookie(setup);

    await app.inject({ method: "POST", url: "/api/auth/logout", headers: { cookie } });

    expect(db.prepare("SELECT COUNT(*) AS c FROM sessions").get().c).toBe(0);
    const status = await app.inject({
      method: "GET",
      url: "/api/auth/status",
      headers: { cookie },
    });
    expect(status.json().data.authenticated).toBe(false);
    await app.close();
  });

  it("rejects an expired session", async () => {
    const { app, db } = makeApp();
    await app.inject({ method: "POST", url: "/api/auth/setup", payload: { password: PASSWORD } });
    db.prepare("INSERT INTO sessions (id, user_id, expires_at) VALUES (?, 'local', ?)").run(
      "sess_expired",
      new Date(Date.now() - 1000).toISOString()
    );
    const res = await app.inject({
      method: "GET",
      url: "/api/auth/status",
      headers: { cookie: `${SESSION_COOKIE}=sess_expired` },
    });
    expect(res.json().data.authenticated).toBe(false);
    await app.close();
  });

  it("returns a 401 envelope for a protected route when unauthenticated", async () => {
    const { app } = makeApp();
    const res = await app.inject({ method: "GET", url: "/api/people" });
    expect(res.statusCode).toBe(401);
    expect(res.json()).toEqual({
      ok: false,
      error: { code: "unauthenticated", message: "Authentication required." },
    });
    await app.close();
  });

  it("allows a protected route with a valid session", async () => {
    const { app } = makeApp();
    const setup = await app.inject({
      method: "POST",
      url: "/api/auth/setup",
      payload: { password: PASSWORD },
    });

    const res = await app.inject({
      method: "GET",
      url: "/api/people",
      headers: { cookie: sessionCookie(setup) },
    });
    expect(res.statusCode).toBe(200);
    expect(res.json().ok).toBe(true);
    await app.close();
  });
});
