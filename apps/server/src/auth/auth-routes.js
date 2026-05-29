import { LoginInput, SetupPasswordInput } from "@em-notes/contracts";
import { validateBody } from "../contracts/route-validation.js";
import { getStatus, setupPassword, login, logout } from "./auth-service.js";

export const SESSION_COOKIE = "em_notes_session";

// HttpOnly + SameSite=Strict; Secure only over HTTPS (§10). maxAge mirrors the session TTL.
function sessionCookieOptions(request, sessionDays) {
  return {
    path: "/",
    httpOnly: true,
    sameSite: "strict",
    secure: request.protocol === "https",
    maxAge: sessionDays * 24 * 60 * 60,
  };
}

export function registerAuthRoutes(app) {
  const { db, config } = app;

  app.get("/api/auth/status", async (request) => ({
    ok: true,
    data: getStatus(db, request.cookies[SESSION_COOKIE]),
  }));

  app.post("/api/auth/setup", async (request, reply) => {
    const validated = validateBody(SetupPasswordInput, request.body);
    if (!validated.ok) return reply.code(400).send(validated);

    const result = await setupPassword(db, validated.value.password, config.sessionDays);
    if (!result.ok) return reply.code(409).send(result);

    reply.setCookie(
      SESSION_COOKIE,
      result.session.id,
      sessionCookieOptions(request, config.sessionDays)
    );
    return { ok: true, data: { configured: true, authenticated: true } };
  });

  app.post("/api/auth/login", async (request, reply) => {
    const validated = validateBody(LoginInput, request.body);
    if (!validated.ok) return reply.code(400).send(validated);

    const result = await login(db, validated.value.password, config.sessionDays);
    if (!result.ok) return reply.code(401).send(result);

    reply.setCookie(
      SESSION_COOKIE,
      result.session.id,
      sessionCookieOptions(request, config.sessionDays)
    );
    return { ok: true, data: { authenticated: true } };
  });

  app.post("/api/auth/logout", async (request, reply) => {
    logout(db, request.cookies[SESSION_COOKIE]);
    reply.clearCookie(SESSION_COOKIE, { path: "/" });
    return { ok: true, data: { authenticated: false } };
  });
}
