import { describe, it, expect, vi, beforeEach } from "vitest";
import { openDb } from "../db.js";
import { migrate } from "../migrations.js";
import { seedDatabase } from "../seed.js";

// Force the embedded-action insert to throw on demand. The repo uses INSERT OR IGNORE, so a
// constraint violation can't be triggered through it (it's swallowed), and CreateNoteInput
// validation strips a textless action before it reaches the DB — so we make insertAction itself
// fail at the DB layer to prove createNote's transaction is all-or-nothing. The flag gates the
// failure so seedDatabase (which also inserts actions) still runs against the real implementation.
let failActionInsert = false;
vi.mock("../repositories/actions-repo.js", async (importOriginal) => {
  const actual = await importOriginal();
  return {
    ...actual,
    insertAction: (db, action) => {
      if (failActionInsert) throw new Error("forced action insert failure");
      return actual.insertAction(db, action);
    },
  };
});

let db, notesService;
beforeEach(async () => {
  failActionInsert = false;
  db = openDb(":memory:");
  migrate(db);
  seedDatabase(db);
  notesService = await import("../services/notes-service.js");
});

const noteCount = (personId) =>
  db.prepare("SELECT COUNT(*) AS c FROM notes WHERE person_id = ?").get(personId).c;

describe("createNote transaction", () => {
  it("rolls back the note when an embedded action insert throws", async () => {
    const before = noteCount("alex");
    failActionInsert = true;
    await expect(
      notesService.createNote(db, {
        personId: "alex",
        summary: "Should not persist.",
        actions: [{ text: "boom" }],
      })
    ).rejects.toThrow();
    failActionInsert = false;
    expect(noteCount("alex")).toBe(before);
  });

  it("commits both the note and its embedded actions atomically on the happy path", async () => {
    const before = noteCount("alex");
    const created = await notesService.createNote(db, {
      personId: "alex",
      summary: "Persists with its action.",
      actions: [{ text: "Follow up next week" }],
    });
    expect(noteCount("alex")).toBe(before + 1);
    expect(created.actions).toHaveLength(1);
    expect(created.actions[0].text).toBe("Follow up next week");
    expect(created.actions[0].personId).toBe("alex");
    expect(created.actions[0].noteId).toBe(created.id);
  });
});
