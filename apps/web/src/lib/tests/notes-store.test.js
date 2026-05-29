import { describe, it, expect, vi, beforeEach } from "vitest";
import { get } from "svelte/store";

// Mock the API module the store talks to.
vi.mock("../api/notes-api.js", () => ({
  listNotes: vi.fn(),
}));

import * as notesApi from "../api/notes-api.js";
import { notes, loadNotes, resetNotes } from "../stores/notes.js";

function deferred() {
  let resolve;
  const promise = new Promise((res) => {
    resolve = res;
  });
  return { promise, resolve };
}

beforeEach(() => {
  vi.clearAllMocks();
  notes.set([]);
});

describe("notes store", () => {
  it("discards a slower, earlier load when a newer one wins (stale-response guard)", async () => {
    const alex = deferred();
    const sam = deferred();
    notesApi.listNotes.mockReturnValueOnce(alex.promise).mockReturnValueOnce(sam.promise);

    const alexLoad = loadNotes("alex");
    const samLoad = loadNotes("sam");

    // Sam (the newer request) resolves first, then the older Alex request resolves late.
    sam.resolve({ ok: true, data: [{ id: "n-sam" }] });
    await samLoad;
    alex.resolve({ ok: true, data: [{ id: "n-alex" }] });
    await alexLoad;

    expect(get(notes)).toEqual([{ id: "n-sam" }]);
  });

  it("resetNotes clears the store to []", () => {
    notes.set([{ id: "n1" }]);
    resetNotes();
    expect(get(notes)).toEqual([]);
  });
});
