import { CreatePersonInput, UpdatePersonInput } from "@em-notes/contracts";
import { validateBody } from "../contracts/route-validation.js";
import * as people from "../services/people-service.js";

const notFound = (reply) =>
  reply.code(404).send({ ok: false, error: { code: "not_found", message: "Not found." } });

export function registerPeopleRoutes(app) {
  const { db } = app;

  app.get("/api/people", async () => ({ ok: true, data: people.listPeople(db) }));

  app.get("/api/people/:id", async (request, reply) => {
    const person = people.getPerson(db, request.params.id);
    if (!person) return notFound(reply);
    return { ok: true, data: person };
  });

  app.post("/api/people", async (request, reply) => {
    const validated = validateBody(CreatePersonInput, request.body);
    if (!validated.ok) return reply.code(400).send(validated);
    return { ok: true, data: await people.createPerson(db, validated.value) };
  });

  app.patch("/api/people/:id", async (request, reply) => {
    const validated = validateBody(UpdatePersonInput, request.body);
    if (!validated.ok) return reply.code(400).send(validated);
    const updated = people.updatePerson(db, request.params.id, validated.value);
    if (!updated) return notFound(reply);
    return { ok: true, data: updated };
  });

  app.delete("/api/people/:id", async (request, reply) => {
    if (!people.deletePerson(db, request.params.id)) return notFound(reply);
    return { ok: true, data: { id: request.params.id, deleted: true } };
  });
}
