import { describe, it, expect, vi, beforeEach } from "vitest";
import { get } from "svelte/store";

vi.mock("../api/settings-api.js", () => ({
  getSettings: vi.fn(),
  updateSettings: vi.fn(),
}));

import * as settingsApi from "../api/settings-api.js";
import { settings, loadSettings, updateSettings } from "../stores/settings.js";

beforeEach(() => {
  vi.clearAllMocks();
  settings.set({ theme: "light", density: "comfortable" });
});

describe("settings store", () => {
  it("loads settings, defaulting any missing fields", async () => {
    settingsApi.getSettings.mockResolvedValue({ ok: true, data: { theme: "dark" } });
    await loadSettings();
    expect(get(settings)).toEqual({ theme: "dark", density: "comfortable" });
  });

  it("merges an updated setting over the stored value", async () => {
    settingsApi.updateSettings.mockResolvedValue({
      ok: true,
      data: { theme: "dark", density: "compact" },
    });
    await updateSettings({ theme: "dark" });
    expect(get(settings)).toEqual({ theme: "dark", density: "compact" });
    expect(settingsApi.updateSettings).toHaveBeenCalledWith({ theme: "dark" });
  });
});
