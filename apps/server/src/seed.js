import { fileURLToPath } from "node:url";
import { seed as defaultSeed } from "@em-notes/prototype-seed";
import { loadConfig } from "./config.js";
import { openDb } from "./db.js";
import { migrate } from "./migrations.js";
import { insertPerson, insertSentimentPoint } from "./repositories/people-repo.js";
import { insertNote } from "./repositories/notes-repo.js";
import { insertAction } from "./repositories/actions-repo.js";

// The prototype sentiment arrays carry scores but no dates, so synthesize weekly timestamps
// backward from a fixed base (keeps seeding deterministic). Documented in docs/deviations.md.
const SENTIMENT_BASE = "2026-05-22";
const WEEK_MS = 7 * 24 * 60 * 60 * 1000;

function weeklyDates(count, base = SENTIMENT_BASE) {
  const baseMs = new Date(`${base}T00:00:00.000Z`).getTime();
  return Array.from({ length: count }, (_, i) =>
    new Date(baseMs - (count - 1 - i) * WEEK_MS).toISOString().slice(0, 10)
  );
}

// Idempotent seed: inserts people (+ derived sentiment_points), notes, and the note-linked
// action_items (§15.2). All inserts are INSERT OR IGNORE on stable prototype ids, so a
// repeat run inserts nothing. Returns counts of rows newly inserted this run.
export function seedDatabase(db, seed = defaultSeed) {
  const counts = { people: 0, sentimentPoints: 0, notes: 0, actions: 0 };

  db.transaction(() => {
    for (const person of seed.people) {
      counts.people += insertPerson(db, person).changes;
      const scores = person.sentiment ?? [];
      const dates = weeklyDates(scores.length);
      scores.forEach((score, i) => {
        counts.sentimentPoints += insertSentimentPoint(db, {
          id: `sent_${person.id}_${i}`,
          personId: person.id,
          score,
          recordedAt: dates[i],
        }).changes;
      });
    }

    for (const note of seed.notes) {
      counts.notes += insertNote(db, note).changes;
      for (const action of note.actions ?? []) {
        counts.actions += insertAction(db, action).changes;
      }
    }
  })();

  return counts;
}

// CLI: `pnpm --filter @em-notes/server db:seed`
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  const config = loadConfig();
  const db = openDb(config.dbPath);
  migrate(db);
  console.log("Seeded:", seedDatabase(db));
  db.close();
}
