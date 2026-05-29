import { CreateCalendarFeedInput } from "@em-notes/contracts";
import { validateBody } from "../contracts/route-validation.js";
import * as calendar from "../services/calendar-service.js";

const notFound = (reply) =>
  reply.code(404).send({ ok: false, error: { code: "not_found", message: "Not found." } });

export function registerCalendarRoutes(app) {
  const { db } = app;

  app.get("/api/calendar/feeds", async () => ({ ok: true, data: calendar.listFeeds(db) }));

  app.post("/api/calendar/feeds", async (request, reply) => {
    const validated = validateBody(CreateCalendarFeedInput, request.body);
    if (!validated.ok) return reply.code(400).send(validated);
    const result = await calendar.addFeed(db, validated.value);
    if (!result.ok) return reply.code(400).send({ ok: false, error: result.error });
    return { ok: true, data: result.data };
  });

  app.delete("/api/calendar/feeds/:id", async (request, reply) => {
    if (!calendar.removeFeed(db, request.params.id)) return notFound(reply);
    return { ok: true, data: { id: request.params.id, deleted: true } };
  });

  app.post("/api/calendar/sync", async () => ({ ok: true, data: await calendar.syncAll(db) }));

  app.get("/api/calendar/agenda", async (request) => {
    const days = Math.min(60, Math.max(1, parseInt(request.query?.days, 10) || 14));
    return { ok: true, data: calendar.getAgenda(db, { days }) };
  });
}
