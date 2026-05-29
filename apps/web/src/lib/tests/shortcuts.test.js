import { describe, it, expect, beforeEach } from "vitest";
import { get } from "svelte/store";
import { handleKeydown } from "../keyboard/shortcuts.js";
import { route } from "../stores/route.js";
import { people } from "../stores/people.js";
import { paletteOpen, newNotePerson, addReportOpen } from "../stores/ui.js";

function press(key, opts = {}) {
  handleKeydown({
    key,
    metaKey: false,
    ctrlKey: false,
    target: { tagName: "BODY", isContentEditable: false },
    preventDefault() {},
    ...opts,
  });
}

beforeEach(() => {
  route.set({ name: "home", personId: null });
  people.set([
    { id: "alex", name: "Alex Park" },
    { id: "sam", name: "Samira Khoury" },
  ]);
  paletteOpen.set(false);
  newNotePerson.set(null);
  addReportOpen.set(false);
});

describe("keyboard shortcuts", () => {
  it("Cmd/Ctrl+K toggles the palette", () => {
    press("k", { metaKey: true });
    expect(get(paletteOpen)).toBe(true);
    press("k", { metaKey: true });
    expect(get(paletteOpen)).toBe(false);
  });

  it("/ opens the palette outside inputs", () => {
    press("/");
    expect(get(paletteOpen)).toBe(true);
  });

  it("does not open the palette while typing in an input", () => {
    press("/", { target: { tagName: "INPUT" } });
    expect(get(paletteOpen)).toBe(false);
  });

  it("Escape closes the palette", () => {
    paletteOpen.set(true);
    press("Escape");
    expect(get(paletteOpen)).toBe(false);
  });

  it("g then t navigates to team", () => {
    press("g");
    press("t");
    expect(get(route).name).toBe("team");
  });

  it("g then a digit jumps to that direct report", () => {
    press("g");
    press("2");
    expect(get(route)).toEqual({ name: "person", personId: "sam" });
  });

  it("n on a person route opens a new note for that person", () => {
    route.set({ name: "person", personId: "alex" });
    press("n");
    expect(get(newNotePerson)?.id).toBe("alex");
  });

  it("n elsewhere falls back to the palette", () => {
    press("n");
    expect(get(paletteOpen)).toBe(true);
  });
});
