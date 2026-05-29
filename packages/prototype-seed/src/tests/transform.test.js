import { describe, it, expect } from "vitest";
import { EM } from "../data.js";
import { buildPeople, buildNotes, buildActions, buildSeed } from "../transform.js";

describe("buildPeople", () => {
  it("returns all prototype people", () => {
    const people = buildPeople(EM);
    expect(people).toHaveLength(EM.TEAM.length);
    expect(people.map((p) => p.id)).toContain("alex");
  });
});

describe("buildNotes", () => {
  const notes = buildNotes(EM);

  it("flattens every person's notes into one array", () => {
    const total = Object.values(EM.NOTES).reduce((n, arr) => n + arr.length, 0);
    expect(notes).toHaveLength(total);
  });

  it("attaches personId to each note matching its group", () => {
    const alexNotes = notes.filter((n) => n.personId === "alex");
    expect(alexNotes.map((n) => n.id)).toEqual(EM.NOTES.alex.map((n) => n.id));
    expect(notes.every((n) => typeof n.personId === "string")).toBe(true);
  });

  it("attaches personId and noteId to each embedded action", () => {
    const action = notes.find((n) => n.actions.length > 0).actions[0];
    expect(action.personId).toBeTruthy();
    expect(action.noteId).toBeTruthy();
  });

  it("does not mutate the source notes", () => {
    expect(EM.NOTES.alex[0].personId).toBeUndefined();
  });
});

describe("buildActions", () => {
  const actions = buildActions(EM);

  it("maps every open action", () => {
    expect(actions).toHaveLength(EM.OPEN_ACTIONS.length);
  });

  it("normalizes person -> personId and dueDate -> dueAt", () => {
    const oa = actions.find((a) => a.id === "oa1");
    expect(oa.personId).toBe("alex");
    expect(oa.dueAt).toBe("2026-05-29");
  });

  it("keeps personId null for team-wide actions", () => {
    const teamWide = actions.find((a) => a.id === "oa9");
    expect(teamWide.personId).toBeNull();
  });
});

describe("buildSeed", () => {
  it("returns people, notes, and actions", () => {
    const seed = buildSeed(EM);
    expect(Object.keys(seed).sort()).toEqual(["actions", "notes", "people"]);
  });
});
