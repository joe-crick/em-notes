import { describe, it, expect, vi, afterEach } from "vitest";
import { apiFetch } from "../api/client.js";

afterEach(() => vi.restoreAllMocks());

describe("apiFetch error handling", () => {
  it("returns a network_error envelope when fetch rejects", async () => {
    const fetchMock = vi.fn(async () => {
      throw new Error("network down");
    });
    vi.stubGlobal("fetch", fetchMock);
    const res = await apiFetch("/people");
    expect(res.ok).toBe(false);
    expect(res.error.code).toBe("network_error");
  });

  it("returns a bad_response envelope when response.json() rejects", async () => {
    const fetchMock = vi.fn(async () => ({
      json: async () => {
        throw new Error("invalid json");
      },
    }));
    vi.stubGlobal("fetch", fetchMock);
    const res = await apiFetch("/people");
    expect(res.ok).toBe(false);
    expect(res.error.code).toBe("bad_response");
  });

  it("returns the envelope as-is for a normal JSON response", async () => {
    const fetchMock = vi.fn(async () => ({
      json: async () => ({ ok: true, data: [{ id: "p1" }] }),
    }));
    vi.stubGlobal("fetch", fetchMock);
    const res = await apiFetch("/people");
    expect(res).toEqual({ ok: true, data: [{ id: "p1" }] });
  });
});
