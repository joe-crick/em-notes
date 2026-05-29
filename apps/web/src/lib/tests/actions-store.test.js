import { describe, it, expect, vi, beforeEach } from "vitest";
import { get } from "svelte/store";

// Mock the API module the store talks to.
vi.mock("../api/actions-api.js", () => ({
  listActions: vi.fn(),
  createAction: vi.fn(),
  updateAction: vi.fn(),
  deleteAction: vi.fn(),
}));

import * as actionsApi from "../api/actions-api.js";
import { actions, loadActions, toggleAction, addAction } from "../stores/actions.js";

beforeEach(() => {
  vi.clearAllMocks();
  actions.set([]);
});

describe("actions store", () => {
  it("loads actions from the API", async () => {
    actionsApi.listActions.mockResolvedValue({ ok: true, data: [{ id: "a1", done: false }] });
    await loadActions();
    expect(get(actions)).toEqual([{ id: "a1", done: false }]);
  });

  it("optimistically toggles done and persists", async () => {
    actions.set([{ id: "a1", done: false }]);
    actionsApi.updateAction.mockResolvedValue({ ok: true });
    await toggleAction("a1");
    expect(get(actions)[0].done).toBe(true);
    expect(actionsApi.updateAction).toHaveBeenCalledWith("a1", { done: true });
  });

  it("reverts the toggle when the request fails", async () => {
    actions.set([{ id: "a1", done: false }]);
    actionsApi.updateAction.mockResolvedValue({ ok: false });
    await toggleAction("a1");
    expect(get(actions)[0].done).toBe(false);
  });

  it("prepends a created action", async () => {
    actions.set([{ id: "a1", done: false }]);
    actionsApi.createAction.mockResolvedValue({ ok: true, data: { id: "a2", done: false } });
    await addAction({ text: "new" });
    expect(get(actions).map((a) => a.id)).toEqual(["a2", "a1"]);
  });
});
