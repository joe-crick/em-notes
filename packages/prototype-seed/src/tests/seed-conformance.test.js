import { describe, it, expect } from "vitest";
import { conform, Person, Note, ActionItem } from "@em-notes/contracts";
import { seed } from "../seed.js";

// Plan §17.1: "Prototype seed data conforms to contracts." Lives here (not in
// @em-notes/contracts) so the seed package — which already depends on contracts — proves
// its own output, avoiding a circular workspace dependency. See docs/deviations.md.

describe("seed conformance", () => {
  it("every seeded person conforms to Person", () => {
    for (const person of seed.people) {
      expect(conform(Person, person), `person ${person.id}`).not.toBe("::invalid");
    }
  });

  it("every seeded note conforms to Note", () => {
    for (const note of seed.notes) {
      expect(conform(Note, note), `note ${note.id}`).not.toBe("::invalid");
    }
  });

  it("every seeded action conforms to ActionItem", () => {
    for (const action of seed.actions) {
      expect(conform(ActionItem, action), `action ${action.id}`).not.toBe("::invalid");
    }
  });

  it("embedded note actions conform to ActionItem", () => {
    for (const note of seed.notes) {
      for (const action of note.actions) {
        expect(conform(ActionItem, action), `note ${note.id} action ${action.id}`).not.toBe(
          "::invalid"
        );
      }
    }
  });
});
