import { loadConfig } from "./config.js";
import { openDb } from "./db.js";
import { migrate } from "./migrations.js";

// CLI: `pnpm --filter @em-notes/server db:migrate`
const config = loadConfig();
const db = openDb(config.dbPath);
const applied = migrate(db);
console.log(applied.length ? `Applied: ${applied.join(", ")}` : "No pending migrations.");
db.close();
