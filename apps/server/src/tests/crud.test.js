import { describe, it, expect, beforeEach } from "vitest";
import { conform, Person } from "@em-notes/contracts";
import { openDb } from "../db.js";
import { migrate } from "../migrations.js";
import { seedDatabase } from "../seed.js";
import { createApp } from "../app.js";
import { SESSION_COOKIE } from "../auth/auth-routes.js";

// A seeded, authenticated app. Returns the cookie header to replay on protected calls.
async function authedApp() {
  const db = openDb(":memory:");
  migrate(db);
  seedDatabase(db);
  const app = createApp({ db, config: { sessionDays: 14 }, logger: false });
  const setup = await app.inject({
    method: "POST",
    url: "/api/auth/setup",
    payload: { password: "correct-horse-battery" },
  });
  const c = setup.cookies.find((c) => c.name === SESSION_COOKIE);
  return { app, db, cookie: `${SESSION_COOKIE}=${c.value}` };
}

let app, db, cookie;
beforeEach(async () => {
  ({ app, db, cookie } = await authedApp());
});

const get = (url) => app.inject({ method: "GET", url, headers: { cookie } });
const send = (method, url, payload) => app.inject({ method, url, headers: { cookie }, payload });

describe("people routes", () => {
  it("lists seeded people, and they conform to the Person contract", async () => {
    const res = await get("/api/people");
    expect(res.statusCode).toBe(200);
    const people = res.json().data;
    expect(people).toHaveLength(6);
    for (const p of people) {
      expect(conform(Person, p), `person ${p.id}`).not.toBe("::invalid");
    }
  });

  it("derives sentiment and openActions on a person", async () => {
    const alex = (await get("/api/people/alex")).json().data;
    expect(alex.sentiment.length).toBeGreaterThan(0);
    expect(typeof alex.openActions).toBe("number");
  });

  it("404s an unknown person", async () => {
    expect((await get("/api/people/nobody")).statusCode).toBe(404);
  });

  it("creates a person (deriving initials) and persists it", async () => {
    const res = await send("POST", "/api/people", { name: "Robin Vega", role: "Engineer II" });
    expect(res.statusCode).toBe(200);
    const created = res.json().data;
    expect(created.initials).toBe("RV");
    expect((await get(`/api/people/${created.id}`)).json().data.name).toBe("Robin Vega");
  });

  it("rejects a person with no name", async () => {
    const res = await send("POST", "/api/people", { role: "Engineer II" });
    expect(res.statusCode).toBe(400);
    expect(res.json()).toMatchObject({ ok: false, error: { code: "invalid_request" } });
  });

  it("patches a person", async () => {
    const res = await send("PATCH", "/api/people/alex", { role: "Staff Engineer" });
    expect(res.json().data.role).toBe("Staff Engineer");
  });

  it("deletes a person and cascades their notes", async () => {
    expect((await send("DELETE", "/api/people/alex")).statusCode).toBe(200);
    expect((await get("/api/people/alex")).statusCode).toBe(404);
    expect(db.prepare("SELECT COUNT(*) AS c FROM notes WHERE person_id='alex'").get().c).toBe(0);
  });
});

describe("notes routes", () => {
  it("lists a person's notes with their action items", async () => {
    const list = (await get("/api/people/alex/notes")).json().data;
    expect(list.length).toBeGreaterThan(0);
    const withActions = list.find((n) => n.actions.length > 0);
    expect(withActions.actions[0]).toHaveProperty("text");
  });

  it("creates a note that persists", async () => {
    const res = await send("POST", "/api/people/alex/notes", {
      type: "1:1",
      summary: "Talked through the migration plan.",
      date: "May 29, 2026",
    });
    expect(res.statusCode).toBe(200);
    const created = res.json().data;
    expect(created.personId).toBe("alex");

    const fetched = (await get(`/api/notes/${created.id}`)).json().data;
    expect(fetched.summary).toBe("Talked through the migration plan.");
  });

  it("rejects a note with no summary", async () => {
    const res = await send("POST", "/api/people/alex/notes", { type: "1:1" });
    expect(res.statusCode).toBe(400);
    expect(res.json().error.code).toBe("invalid_request");
  });

  it("deletes a note", async () => {
    expect((await send("DELETE", "/api/notes/n1")).statusCode).toBe(200);
    expect((await get("/api/notes/n1")).statusCode).toBe(404);
  });
});

describe("actions routes", () => {
  it("lists actions", async () => {
    expect((await get("/api/actions")).json().data.length).toBeGreaterThan(0);
  });

  it("toggles an action's done state and persists it", async () => {
    const before = (await get("/api/actions")).json().data.find((a) => a.id === "a1");
    const res = await send("PATCH", "/api/actions/a1", { done: !before.done });
    expect(res.statusCode).toBe(200);
    expect(res.json().data.done).toBe(!before.done);

    const after = (await get("/api/actions")).json().data.find((a) => a.id === "a1");
    expect(after.done).toBe(!before.done);
  });

  it("creates an action and rejects empty text", async () => {
    const ok = await send("POST", "/api/actions", { text: "Follow up with Sam", personId: "sam" });
    expect(ok.statusCode).toBe(200);
    const bad = await send("POST", "/api/actions", { text: "" });
    expect(bad.statusCode).toBe(400);
  });
});

describe("settings routes", () => {
  it("returns {} initially, then persists a patch", async () => {
    expect((await get("/api/settings")).json().data).toEqual({});
    const res = await send("PATCH", "/api/settings", { theme: "dark" });
    expect(res.json().data).toMatchObject({ theme: "dark" });
    expect((await get("/api/settings")).json().data).toMatchObject({ theme: "dark" });
  });

  it("rejects an invalid setting value", async () => {
    expect((await send("PATCH", "/api/settings", { theme: "chartreuse" })).statusCode).toBe(400);
  });
});

describe("auth guard", () => {
  it("blocks CRUD routes without a session", async () => {
    const res = await app.inject({ method: "GET", url: "/api/people" });
    expect(res.statusCode).toBe(401);
    expect(res.json().error.code).toBe("unauthenticated");
  });
});
