import { describe, it, expect, beforeEach } from "vitest";
import { openDb } from "../db.js";
import { migrate } from "../migrations.js";
import { seedDatabase } from "../seed.js";
import * as calendarRepo from "../repositories/calendar-repo.js";
import * as peopleRepo from "../repositories/people-repo.js";
import * as notesRepo from "../repositories/notes-repo.js";
import { applyFeedEvents, getAgenda } from "../services/calendar-service.js";

// Fixed clock so the sync window / prep horizon are deterministic.
const NOW = Date.UTC(2026, 5, 1, 9, 0, 0); // 2026-06-01 09:00:00Z

// Format a UTC ms timestamp as an ICS UTC date-time (YYYYMMDDTHHMMSSZ).
function icsUtc(ms) {
  return new Date(ms)
    .toISOString()
    .replace(/[-:]/g, "")
    .replace(/\.\d{3}Z$/, "Z");
}

// Build a single-VEVENT feed body.
function feedIcs({ uid = "evt-1", summary, start, end, attendees = [] }) {
  const lines = ["BEGIN:VCALENDAR", "VERSION:2.0", "BEGIN:VEVENT", `UID:${uid}`];
  if (summary != null) lines.push(`SUMMARY:${summary}`);
  lines.push(`DTSTART:${icsUtc(start)}`);
  if (end != null) lines.push(`DTEND:${icsUtc(end)}`);
  for (const a of attendees) lines.push(`ATTENDEE;CN=${a.name}:mailto:${a.email}`);
  lines.push("END:VEVENT", "END:VCALENDAR");
  return lines.join("\r\n");
}

let db, feed, alex;
beforeEach(() => {
  db = openDb(":memory:");
  migrate(db);
  seedDatabase(db);
  alex = peopleRepo.getPerson(db, "alex");
  feed = calendarRepo.insertFeed(db, {
    id: "feed1",
    url: "https://cal.example.com/a.ics",
    label: "Cal",
  });
});

const EVENT_START = NOW + 2 * 60 * 60 * 1000; // 2026-06-01 11:00:00Z

describe("calendar sync — seeded person", () => {
  it("seeds Alex Park with the expected email", () => {
    expect(alex.email).toBe("alex.park@example.com");
  });
});

describe("calendar sync — attendee email match", () => {
  let ics;
  beforeEach(() => {
    ics = feedIcs({
      summary: "1:1",
      start: EVENT_START,
      end: EVENT_START + 30 * 60000,
      attendees: [{ name: "Alex Park", email: "alex.park@example.com" }],
    });
  });

  it("matches the event to the person by attendee email and shows it in the agenda", async () => {
    await applyFeedEvents(db, feed, ics, NOW);
    const agenda = getAgenda(db, { nowMs: NOW });
    const ev = agenda.find((e) => e.startsAt === new Date(EVENT_START).toISOString());
    expect(ev).toBeTruthy();
    expect(ev.personId).toBe(alex.id);
  });

  it("auto-creates a 'Prep for 1:1' note for the matched person dated to the event day", async () => {
    await applyFeedEvents(db, feed, ics, NOW);
    const notes = notesRepo.listNotesByPerson(db, alex.id);
    const prep = notes.find((nt) => nt.summary.startsWith("Prep for 1:1"));
    expect(prep).toBeTruthy();
    expect(prep.date).toBe("June 1, 2026");
  });

  it("does not create a second prep note when the same feed is synced again (dedup by person+date)", async () => {
    await applyFeedEvents(db, feed, ics, NOW);
    await applyFeedEvents(db, feed, ics, NOW);
    const prepNotes = notesRepo
      .listNotesByPerson(db, alex.id)
      .filter((nt) => nt.summary.startsWith("Prep for 1:1"));
    expect(prepNotes).toHaveLength(1);
  });
});

describe("calendar sync — matching fallbacks", () => {
  it("matches by full name in the summary when no attendee email matches", async () => {
    const ics = feedIcs({
      uid: "name-match",
      summary: "1:1 with Alex Park",
      start: EVENT_START,
      end: EVENT_START + 30 * 60000,
      attendees: [{ name: "Someone Else", email: "noone@elsewhere.test" }],
    });
    await applyFeedEvents(db, feed, ics, NOW);
    const agenda = getAgenda(db, { nowMs: NOW });
    const ev = agenda.find((e) => e.startsAt === new Date(EVENT_START).toISOString());
    expect(ev).toBeTruthy();
    expect(ev.personId).toBe(alex.id);
  });

  it("leaves personId null for an unmatched event but still lists it in the agenda", async () => {
    const ics = feedIcs({
      uid: "unmatched",
      summary: "External vendor demo",
      start: EVENT_START,
      end: EVENT_START + 30 * 60000,
      attendees: [{ name: "Vendor Rep", email: "rep@vendor.test" }],
    });
    await applyFeedEvents(db, feed, ics, NOW);
    const agenda = getAgenda(db, { nowMs: NOW });
    const ev = agenda.find((e) => e.startsAt === new Date(EVENT_START).toISOString());
    expect(ev).toBeTruthy();
    expect(ev.personId).toBeNull();
  });
});
