import Fastify from "fastify";
import cookie from "@fastify/cookie";
import { registerHealthRoutes } from "./routes/health-routes.js";
import { registerAuthGuard } from "./auth/auth-guard.js";
import { registerAuthRoutes } from "./auth/auth-routes.js";
import { registerPeopleRoutes } from "./routes/people-routes.js";
import { registerNotesRoutes } from "./routes/notes-routes.js";
import { registerActionsRoutes } from "./routes/actions-routes.js";
import { registerSettingsRoutes } from "./routes/settings-routes.js";
import { registerCalendarRoutes } from "./routes/calendar-routes.js";

// Fastify app factory (plan §14). `db` and `config` are decorated onto the instance so
// route plugins can reach them. CRUD routes are registered in later phases (and are
// automatically protected by the auth guard).
export function createApp({ db, config, logger = true }) {
  const app = Fastify({ logger });

  app.register(cookie);
  app.decorate("db", db);
  app.decorate("config", config);

  registerAuthGuard(app);
  registerHealthRoutes(app);
  registerAuthRoutes(app);
  registerPeopleRoutes(app);
  registerNotesRoutes(app);
  registerActionsRoutes(app);
  registerSettingsRoutes(app);
  registerCalendarRoutes(app);

  return app;
}
