import { describe, it, expect, vi, afterEach } from "vitest";
import { apiFetch } from "../api/client.js";

afterEach(() => vi.restoreAllMocks());

function mockFetch(jsonBody = { ok: true }) {
  const fetchMock = vi.fn(async () => ({ json: async () => jsonBody }));
  vi.stubGlobal("fetch", fetchMock);
  return fetchMock;
}

describe("apiFetch", () => {
  it("prefixes /api and sends credentials: include", async () => {
    const fetchMock = mockFetch();
    await apiFetch("/people");
    const [url, options] = fetchMock.mock.calls[0];
    expect(url).toBe("/api/people");
    expect(options.credentials).toBe("include");
  });

  it("sets a JSON content-type for a body and preserves caller headers", async () => {
    const fetchMock = mockFetch();
    await apiFetch("/auth/login", {
      method: "POST",
      body: JSON.stringify({ password: "x" }),
      headers: { "X-Test": "1" },
    });
    const [, options] = fetchMock.mock.calls[0];
    expect(options.method).toBe("POST");
    expect(options.headers["Content-Type"]).toBe("application/json");
    expect(options.headers["X-Test"]).toBe("1");
  });

  it("omits Content-Type on a bodyless request (e.g. logout)", async () => {
    const fetchMock = mockFetch();
    await apiFetch("/auth/logout", { method: "POST" });
    const [, options] = fetchMock.mock.calls[0];
    expect(options.headers["Content-Type"]).toBeUndefined();
  });

  it("returns the parsed JSON body", async () => {
    mockFetch({ ok: true, data: { status: "ok" } });
    expect(await apiFetch("/health")).toEqual({ ok: true, data: { status: "ok" } });
  });
});
