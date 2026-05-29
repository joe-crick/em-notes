import { describe, it, expect } from "vitest";
import { get } from "svelte/store";
import { route, goTo } from "../stores/route.js";

describe("route store", () => {
  it("starts on home", () => {
    expect(get(route)).toEqual({ name: "home", personId: null });
  });

  it("navigates to top-level routes", () => {
    for (const name of ["team", "actions", "settings", "home"]) {
      goTo(name);
      expect(get(route)).toEqual({ name, personId: null });
    }
  });

  it("carries a personId when navigating to a person", () => {
    goTo("person", "alex");
    expect(get(route)).toEqual({ name: "person", personId: "alex" });
  });
});
