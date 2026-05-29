import { describe, it, expect, vi, beforeEach } from "vitest";
import { get } from "svelte/store";

// Mock the API module the store talks to.
vi.mock("../api/calendar-api.js", () => ({
  listFeeds: vi.fn(),
  addFeed: vi.fn(),
  removeFeed: vi.fn(),
  syncFeeds: vi.fn(),
  getAgenda: vi.fn(),
}));

import * as calendarApi from "../api/calendar-api.js";
import {
  feeds,
  agenda,
  loadFeeds,
  loadAgenda,
  addFeed,
  removeFeed,
  syncFeeds,
  resetCalendar,
} from "../stores/calendar.js";

beforeEach(() => {
  vi.clearAllMocks();
  feeds.set([]);
  agenda.set([]);
});

describe("calendar store", () => {
  it("loads feeds from the API", async () => {
    calendarApi.listFeeds.mockResolvedValue({ ok: true, data: [{ id: "f1", url: "u1" }] });
    await loadFeeds();
    expect(get(feeds)).toEqual([{ id: "f1", url: "u1" }]);
  });

  it("loads the agenda from the API", async () => {
    calendarApi.getAgenda.mockResolvedValue({ ok: true, data: [{ id: "e1", title: "Standup" }] });
    await loadAgenda();
    expect(get(agenda)).toEqual([{ id: "e1", title: "Standup" }]);
  });

  it("adds a feed and reloads feeds + agenda", async () => {
    calendarApi.addFeed.mockResolvedValue({ ok: true, data: { id: "f2" } });
    calendarApi.listFeeds.mockResolvedValue({ ok: true, data: [{ id: "f2", url: "u2" }] });
    calendarApi.getAgenda.mockResolvedValue({ ok: true, data: [{ id: "e2", title: "New" }] });

    await addFeed("u2", "Work");

    expect(calendarApi.addFeed).toHaveBeenCalledWith("u2", "Work");
    expect(get(feeds)).toEqual([{ id: "f2", url: "u2" }]);
    expect(get(agenda)).toEqual([{ id: "e2", title: "New" }]);
  });

  it("removes a feed and reloads feeds + agenda", async () => {
    calendarApi.removeFeed.mockResolvedValue({ ok: true });
    calendarApi.listFeeds.mockResolvedValue({ ok: true, data: [{ id: "f3", url: "u3" }] });
    calendarApi.getAgenda.mockResolvedValue({ ok: true, data: [{ id: "e3", title: "Kept" }] });

    await removeFeed("f1");

    expect(calendarApi.removeFeed).toHaveBeenCalledWith("f1");
    expect(get(feeds)).toEqual([{ id: "f3", url: "u3" }]);
    expect(get(agenda)).toEqual([{ id: "e3", title: "Kept" }]);
  });

  it("syncs feeds then reloads feeds + agenda", async () => {
    calendarApi.syncFeeds.mockResolvedValue({ ok: true });
    calendarApi.listFeeds.mockResolvedValue({ ok: true, data: [{ id: "f4", url: "u4" }] });
    calendarApi.getAgenda.mockResolvedValue({ ok: true, data: [{ id: "e4", title: "Synced" }] });

    await syncFeeds();

    expect(calendarApi.syncFeeds).toHaveBeenCalled();
    expect(get(feeds)).toEqual([{ id: "f4", url: "u4" }]);
    expect(get(agenda)).toEqual([{ id: "e4", title: "Synced" }]);
  });

  it("resets both stores to empty arrays", () => {
    feeds.set([{ id: "f1" }]);
    agenda.set([{ id: "e1" }]);
    resetCalendar();
    expect(get(feeds)).toEqual([]);
    expect(get(agenda)).toEqual([]);
  });
});
