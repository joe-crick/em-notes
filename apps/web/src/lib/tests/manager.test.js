import { describe, it, expect } from "vitest";
import { ME, greeting } from "../manager.js";

describe("manager", () => {
  it("exposes a fixed single-user identity", () => {
    expect(ME.initials).toBe("JC");
    expect(ME.name).toContain("Joe");
  });

  it("greets by time of day", () => {
    expect(greeting(new Date(2026, 4, 29, 8))).toBe("Good morning");
    expect(greeting(new Date(2026, 4, 29, 14))).toBe("Good afternoon");
    expect(greeting(new Date(2026, 4, 29, 21))).toBe("Good evening");
  });
});
