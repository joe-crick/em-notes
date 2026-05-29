import { describe, it, expect } from "vitest";
import { openDb } from "../db.js";
import { migrate } from "../migrations.js";
import { seedDatabase } from "../seed.js";

const EXPECTED_TABLES = [
  "app_meta",
  "auth_user",
  "sessions",
  "people",
  "sentiment_points",
  "notes",
  "action_items",
  "goals",
  "feedback",
  "settings",
];

const tableNames = (db) =>
  db
    .prepare("SELECT name FROM sqlite_master WHERE type = 'table'")
    .all()
    .map((r) => r.name);

const count = (db, table) => db.prepare(`SELECT COUNT(*) AS c FROM ${table}`).get().c;

describe("migrate", () => {
  it("creates all expected tables", () => {
    const db = openDb(":memory:");
    migrate(db);
    for (const t of EXPECTED_TABLES) expect(tableNames(db)).toContain(t);
  });

  it("is idempotent (nothing pending on a second run)", () => {
    const db = openDb(":memory:");
    expect(migrate(db).length).toBeGreaterThan(0);
    expect(migrate(db)).toEqual([]);
  });
});

describe("seedDatabase", () => {
  it("inserts prototype people, notes, actions, and sentiment points", () => {
    const db = openDb(":memory:");
    migrate(db);
    const inserted = seedDatabase(db);

    expect(inserted.people).toBe(6);
    expect(count(db, "people")).toBe(6);
    expect(count(db, "notes")).toBeGreaterThan(0);
    expect(count(db, "action_items")).toBeGreaterThan(0);
    expect(count(db, "sentiment_points")).toBeGreaterThan(0);
  });

  it("does not duplicate rows on a repeat run", () => {
    const db = openDb(":memory:");
    migrate(db);
    seedDatabase(db);
    const peopleBefore = count(db, "people");

    const second = seedDatabase(db);

    expect(second.people).toBe(0);
    expect(count(db, "people")).toBe(peopleBefore);
  });

  it("maps camelCase domain fields to snake_case columns", () => {
    const db = openDb(":memory:");
    migrate(db);
    seedDatabase(db);

    const alex = db.prepare("SELECT * FROM people WHERE id = 'alex'").get();
    expect(alex.next_one_on_one).toBe("Tue 10:00");
    expect(JSON.parse(alex.tags_json)).toContain("payments");
  });

  it("links seeded actions to their note and person", () => {
    const db = openDb(":memory:");
    migrate(db);
    seedDatabase(db);

    const a1 = db.prepare("SELECT * FROM action_items WHERE id = 'a1'").get();
    expect(a1.note_id).toBe("n1");
    expect(a1.person_id).toBe("alex");
  });
});
