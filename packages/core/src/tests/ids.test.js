import { describe, it, expect } from "vitest";
import { createId } from "../ids.js";

describe("createId", () => {
  it("prefixes the id with the given namespace", () => {
    expect(createId("note")).toMatch(/^note_/);
  });

  it("returns a bare id when no prefix is given", () => {
    const id = createId();
    expect(typeof id).toBe("string");
    expect(id.length).toBeGreaterThan(0);
    expect(id).not.toContain("_");
  });

  it("generates unique ids across calls", () => {
    const ids = new Set(Array.from({ length: 1000 }, () => createId("person")));
    expect(ids.size).toBe(1000);
  });
});
