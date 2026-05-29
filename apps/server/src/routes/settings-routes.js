import { UserSettings } from "@em-notes/contracts";
import { validateBody } from "../contracts/route-validation.js";
import * as settings from "../services/settings-service.js";

export function registerSettingsRoutes(app) {
  const { db } = app;

  app.get("/api/settings", async () => ({ ok: true, data: settings.getSettings(db) }));

  app.patch("/api/settings", async (request, reply) => {
    const validated = validateBody(UserSettings, request.body);
    if (!validated.ok) return reply.code(400).send(validated);
    return { ok: true, data: settings.updateSettings(db, validated.value) };
  });
}
