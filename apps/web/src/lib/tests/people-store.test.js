import { describe, it, expect, vi, beforeEach } from "vitest";
import { get } from "svelte/store";

vi.mock("../api/people-api.js", () => ({
  listPeople: vi.fn(),
  createPerson: vi.fn(),
  updatePerson: vi.fn(),
  deletePerson: vi.fn(),
}));

import * as peopleApi from "../api/people-api.js";
import { people, loadPeople, updatePerson, deletePerson, resetPeople } from "../stores/people.js";

beforeEach(() => {
  vi.clearAllMocks();
  resetPeople();
});

describe("people store", () => {
  it("reloads the roster after a successful update", async () => {
    peopleApi.updatePerson.mockResolvedValue({ ok: true, data: { id: "alex" } });
    peopleApi.listPeople.mockResolvedValue({
      ok: true,
      data: [{ id: "alex", name: "Alexandra Park" }],
    });
    const res = await updatePerson("alex", { name: "Alexandra Park" });
    expect(res.ok).toBe(true);
    expect(peopleApi.updatePerson).toHaveBeenCalledWith("alex", { name: "Alexandra Park" });
    expect(get(people)).toEqual([{ id: "alex", name: "Alexandra Park" }]);
  });

  it("does not reload when an update fails", async () => {
    peopleApi.updatePerson.mockResolvedValue({ ok: false, error: { code: "x" } });
    const res = await updatePerson("alex", { name: "" });
    expect(res.ok).toBe(false);
    expect(peopleApi.listPeople).not.toHaveBeenCalled();
  });

  it("reloads the roster after a successful delete", async () => {
    peopleApi.deletePerson.mockResolvedValue({ ok: true, data: { deleted: true } });
    peopleApi.listPeople.mockResolvedValue({ ok: true, data: [] });
    const res = await deletePerson("alex");
    expect(res.ok).toBe(true);
    expect(peopleApi.deletePerson).toHaveBeenCalledWith("alex");
    expect(get(people)).toEqual([]);
  });
});
