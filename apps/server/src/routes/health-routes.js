// Health check — no auth, no DB access. Uses the standard { ok, data } envelope (§11).
export function registerHealthRoutes(app) {
  app.get("/api/health", async () => ({ ok: true, data: { status: "ok" } }));
}
