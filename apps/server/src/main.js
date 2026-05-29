import { loadConfig } from "./config.js";
import { openDb } from "./db.js";
import { migrate } from "./migrations.js";
import { createApp } from "./app.js";
import { syncAll } from "./services/calendar-service.js";

// Entrypoint: open the DB, ensure migrations are applied (safe/idempotent), serve.
const config = loadConfig();
const db = openDb(config.dbPath);
migrate(db);

const app = createApp({ db, config });

// Refresh subscribed calendar feeds on boot and every 15 minutes (best-effort, non-blocking).
const SYNC_INTERVAL_MS = 15 * 60 * 1000;
function refreshCalendars() {
  syncAll(db).catch((err) => app.log.warn({ err }, "calendar sync failed"));
}

app
  .listen({ host: config.host, port: config.port })
  .then(() => {
    refreshCalendars();
    const timer = setInterval(refreshCalendars, SYNC_INTERVAL_MS);
    timer.unref?.();
  })
  .catch((err) => {
    app.log.error(err);
    process.exit(1);
  });
