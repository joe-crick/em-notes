import { describe, it, expect } from "vitest";
import { openDb } from "../db.js";
import { migrate } from "../migrations.js";
import { createApp } from "../app.js";

describe("GET /api/health", () => {
  it("returns the ok envelope", async () => {
    const db = openDb(":memory:");
    migrate(db);
    const app = createApp({ db, config: {}, logger: false });

    const res = await app.inject({ method: "GET", url: "/api/health" });

    expect(res.statusCode).toBe(200);
    expect(res.json()).toEqual({ ok: true, data: { status: "ok" } });

    await app.close();
  });
});
