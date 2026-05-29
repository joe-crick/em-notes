import { describe, it, expect, vi, beforeEach } from "vitest";
import { get } from "svelte/store";

vi.mock("../api/settings-api.js", () => ({
  getSettings: vi.fn(),
  updateSettings: vi.fn(),
}));

import * as settingsApi from "../api/settings-api.js";
import { settings, loadSettings, updateSettings } from "../stores/settings.js";

// teamName defaults to ME.team when the API doesn't supply one.
const TEAM = "Payments Platform";

beforeEach(() => {
  vi.clearAllMocks();
  settings.set({ theme: "light", density: "comfortable", teamName: TEAM });
});

describe("settings store", () => {
  it("loads settings, defaulting any missing fields", async () => {
    settingsApi.getSettings.mockResolvedValue({ ok: true, data: { theme: "dark" } });
    await loadSettings();
    expect(get(settings)).toEqual({ theme: "dark", density: "comfortable", teamName: TEAM });
  });

  it("merges an updated setting over the stored value", async () => {
    settingsApi.updateSettings.mockResolvedValue({
      ok: true,
      data: { theme: "dark", density: "compact" },
    });
    await updateSettings({ theme: "dark" });
    expect(get(settings)).toEqual({ theme: "dark", density: "compact", teamName: TEAM });
    expect(settingsApi.updateSettings).toHaveBeenCalledWith({ theme: "dark" });
  });

  it("persists a team rename", async () => {
    settingsApi.updateSettings.mockResolvedValue({
      ok: true,
      data: { theme: "light", density: "comfortable", teamName: "Core Infra" },
    });
    await updateSettings({ teamName: "Core Infra" });
    expect(get(settings).teamName).toBe("Core Infra");
    expect(settingsApi.updateSettings).toHaveBeenCalledWith({ teamName: "Core Infra" });
  });
});
