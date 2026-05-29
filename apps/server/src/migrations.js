import { readdirSync, readFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const MIGRATIONS_DIR = join(dirname(fileURLToPath(import.meta.url)), "..", "migrations");

// Apply any plain-SQL migrations in `migrations/` (sorted by filename) that haven't run yet,
// each in its own transaction, tracking applied files in `schema_migrations`. Idempotent:
// returns the list of files newly applied this call.
export function migrate(db, dir = MIGRATIONS_DIR) {
  db.exec(
    `CREATE TABLE IF NOT EXISTS schema_migrations (
       id TEXT PRIMARY KEY,
       applied_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
     )`
  );

  const files = readdirSync(dir)
    .filter((f) => f.endsWith(".sql"))
    .sort();
  const applied = new Set(
    db
      .prepare("SELECT id FROM schema_migrations")
      .all()
      .map((r) => r.id)
  );
  const record = db.prepare("INSERT INTO schema_migrations (id) VALUES (?)");

  const newlyApplied = [];
  for (const file of files) {
    if (applied.has(file)) continue;
    const sql = readFileSync(join(dir, file), "utf8");
    db.transaction(() => {
      db.exec(sql);
      record.run(file);
    })();
    newlyApplied.push(file);
  }
  return newlyApplied;
}
