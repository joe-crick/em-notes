import { loadConfig } from "./config.js";
import { openDb } from "./db.js";
import { migrate } from "./migrations.js";
import { createApp } from "./app.js";

// Entrypoint: open the DB, ensure migrations are applied (safe/idempotent), serve.
const config = loadConfig();
const db = openDb(config.dbPath);
migrate(db);

const app = createApp({ db, config });

app.listen({ host: config.host, port: config.port }).catch((err) => {
  app.log.error(err);
  process.exit(1);
});
