import { getValidSession } from "./sessions.js";
import { SESSION_COOKIE } from "./auth-routes.js";

// Routes that don't require a session.
const PUBLIC_PATHS = new Set([
  "/api/health",
  "/api/auth/status",
  "/api/auth/setup",
  "/api/auth/login",
  "/api/auth/logout",
]);

// All non-auth /api routes require a valid session (§10). Registered as an onRequest hook;
// it parses the cookie via `app.parseCookie` rather than `request.cookies` to avoid
// depending on @fastify/cookie's own onRequest hook running first.
export function registerAuthGuard(app) {
  app.addHook("onRequest", async (request, reply) => {
    const path = request.url.split("?")[0];
    if (!path.startsWith("/api/") || PUBLIC_PATHS.has(path)) return;

    const cookies = request.headers.cookie ? app.parseCookie(request.headers.cookie) : {};
    const session = getValidSession(app.db, cookies[SESSION_COOKIE]);
    if (!session) {
      const error = { code: "unauthenticated", message: "Authentication required." };
      return reply.code(401).send({ ok: false, error });
    }
    request.session = session;
  });
}
