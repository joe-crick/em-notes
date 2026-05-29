import { CreateNoteInput, UpdateNoteInput } from "@em-notes/contracts";
import { validateBody } from "../contracts/route-validation.js";
import * as notes from "../services/notes-service.js";
import * as people from "../services/people-service.js";

const notFound = (reply) =>
  reply.code(404).send({ ok: false, error: { code: "not_found", message: "Not found." } });

export function registerNotesRoutes(app) {
  const { db } = app;

  app.get("/api/people/:id/notes", async (request, reply) => {
    if (!people.getPerson(db, request.params.id)) return notFound(reply);
    return { ok: true, data: notes.listNotesByPerson(db, request.params.id) };
  });

  // personId comes from the route param, not the body (plan §18).
  app.post("/api/people/:id/notes", async (request, reply) => {
    const validated = validateBody(CreateNoteInput, {
      ...request.body,
      personId: request.params.id,
    });
    if (!validated.ok) return reply.code(400).send(validated);
    if (!people.getPerson(db, request.params.id)) return notFound(reply);
    return { ok: true, data: await notes.createNote(db, validated.value) };
  });

  app.get("/api/notes/:id", async (request, reply) => {
    const note = notes.getNote(db, request.params.id);
    if (!note) return notFound(reply);
    return { ok: true, data: note };
  });

  app.patch("/api/notes/:id", async (request, reply) => {
    const validated = validateBody(UpdateNoteInput, request.body);
    if (!validated.ok) return reply.code(400).send(validated);
    const updated = notes.updateNote(db, request.params.id, validated.value);
    if (!updated) return notFound(reply);
    return { ok: true, data: updated };
  });

  app.delete("/api/notes/:id", async (request, reply) => {
    if (!notes.deleteNote(db, request.params.id)) return notFound(reply);
    return { ok: true, data: { id: request.params.id, deleted: true } };
  });
}
