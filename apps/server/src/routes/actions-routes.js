import { CreateActionInput, UpdateActionInput } from "@em-notes/contracts";
import { validateBody } from "../contracts/route-validation.js";
import * as actions from "../services/actions-service.js";

const notFound = (reply) =>
  reply.code(404).send({ ok: false, error: { code: "not_found", message: "Not found." } });

export function registerActionsRoutes(app) {
  const { db } = app;

  app.get("/api/actions", async () => ({ ok: true, data: actions.listActions(db) }));

  app.post("/api/actions", async (request, reply) => {
    const validated = validateBody(CreateActionInput, request.body);
    if (!validated.ok) return reply.code(400).send(validated);
    return { ok: true, data: await actions.createAction(db, validated.value) };
  });

  app.patch("/api/actions/:id", async (request, reply) => {
    const validated = validateBody(UpdateActionInput, request.body);
    if (!validated.ok) return reply.code(400).send(validated);
    const updated = actions.updateAction(db, request.params.id, validated.value);
    if (!updated) return notFound(reply);
    return { ok: true, data: updated };
  });

  app.delete("/api/actions/:id", async (request, reply) => {
    if (!actions.deleteAction(db, request.params.id)) return notFound(reply);
    return { ok: true, data: { id: request.params.id, deleted: true } };
  });
}
