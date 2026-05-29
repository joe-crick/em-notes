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
    const result = await actions.createAction(db, validated.value);
    if (!result.ok) return reply.code(400).send({ ok: false, error: result.error });
    return { ok: true, data: result.data };
  });

  app.patch("/api/actions/:id", async (request, reply) => {
    const validated = validateBody(UpdateActionInput, request.body);
    if (!validated.ok) return reply.code(400).send(validated);
    const result = actions.updateAction(db, request.params.id, validated.value);
    if (result.notFound) return notFound(reply);
    if (!result.ok) return reply.code(400).send({ ok: false, error: result.error });
    return { ok: true, data: result.data };
  });

  app.delete("/api/actions/:id", async (request, reply) => {
    if (!actions.deleteAction(db, request.params.id)) return notFound(reply);
    return { ok: true, data: { id: request.params.id, deleted: true } };
  });
}
