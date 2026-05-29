import { createId } from "@em-notes/core";
import * as calendarRepo from "../repositories/calendar-repo.js";
import * as peopleRepo from "../repositories/people-repo.js";
import * as notesRepo from "../repositories/notes-repo.js";
import { createNote } from "./notes-service.js";
import { eventsInWindow } from "../calendar/ics.js";

const DAY = 86400000;
const SYNC_BACK = 1 * DAY; // include events from yesterday onward
const SYNC_FORWARD = 30 * DAY;
const PREP_HORIZON = 24 * 60 * 60 * 1000; // auto-create prep notes for the next 24h

// Date label matching the seed/notes convention ("May 29, 2026"), in UTC so it lines up with the
// wall-clock the ICS parser preserved.
function dateLabel(iso) {
  return new Intl.DateTimeFormat("en-US", {
    month: "long",
    day: "numeric",
    year: "numeric",
    timeZone: "UTC",
  }).format(new Date(iso));
}

// --- feeds management -----------------------------------------------------

export function listFeeds(db) {
  return calendarRepo.listFeeds(db);
}

function hostLabel(url) {
  try {
    return new URL(url).host || url;
  } catch {
    return url;
  }
}

// Returns { ok: true, data } | { ok: false, error }.
export async function addFeed(db, input, opts = {}) {
  let parsed;
  try {
    parsed = new URL(input.url);
  } catch {
    return { ok: false, error: { code: "invalid_request", message: "That isn't a valid URL." } };
  }
  if (parsed.protocol !== "http:" && parsed.protocol !== "https:") {
    return { ok: false, error: { code: "invalid_request", message: "Feed URL must be http(s)." } };
  }
  const feed = calendarRepo.insertFeed(db, {
    id: createId("feed"),
    url: input.url,
    label: input.label?.trim() || hostLabel(input.url),
  });
  // Best-effort initial sync; a fetch failure is recorded on the feed, not fatal to adding it.
  await syncFeed(db, feed, opts);
  return { ok: true, data: calendarRepo.getFeed(db, feed.id) };
}

export function removeFeed(db, id) {
  return calendarRepo.deleteFeed(db, id);
}

// --- matching -------------------------------------------------------------

function buildPeopleIndex(db) {
  const people = peopleRepo.listPeople(db);
  const byEmail = new Map();
  const byName = new Map();
  for (const p of people) {
    if (p.email) byEmail.set(p.email.toLowerCase(), p.id);
    byName.set(p.name.toLowerCase(), p.id);
  }
  return { people, byEmail, byName };
}

function matchPersonId(event, index) {
  for (const a of event.attendees ?? []) {
    if (a.email && index.byEmail.has(a.email)) return index.byEmail.get(a.email);
    if (a.name && index.byName.has(a.name.toLowerCase()))
      return index.byName.get(a.name.toLowerCase());
  }
  const summary = (event.summary ?? "").toLowerCase();
  for (const p of index.people) {
    if (summary.includes(p.name.toLowerCase())) return p.id;
  }
  return null;
}

// --- sync core (testable: takes ICS text, no network) ---------------------

export async function applyFeedEvents(db, feed, icsText, nowMs = Date.now()) {
  const index = buildPeopleIndex(db);
  const instances = eventsInWindow(icsText, nowMs - SYNC_BACK, nowMs + SYNC_FORWARD).map((e) => ({
    id: createId("cevent"),
    uid: e.uid,
    summary: e.summary,
    location: e.location,
    startsAt: e.startsAt,
    endsAt: e.endsAt,
    personId: matchPersonId(e, index),
    attendees: (e.attendees ?? []).map((a) => a.email).filter(Boolean),
  }));

  calendarRepo.replaceFeedEvents(db, feed.id, instances);
  await autoCreatePrepNotes(db, instances, nowMs);
  calendarRepo.setFeedStatus(db, feed.id, {
    lastSyncedAt: new Date(nowMs).toISOString(),
    lastError: null,
  });
  return { count: instances.length };
}

// For each matched event in the next 24h, ensure a prep note exists for that person on that date
// (dedup by person + date, so repeated syncs don't pile up notes).
async function autoCreatePrepNotes(db, events, nowMs) {
  const created = new Set();
  for (const e of events) {
    if (!e.personId) continue;
    const startMs = Date.parse(e.startsAt);
    if (startMs < nowMs || startMs > nowMs + PREP_HORIZON) continue;

    const label = dateLabel(e.startsAt);
    const key = `${e.personId}|${label}`;
    if (created.has(key)) continue;
    const existing = notesRepo.listNotesByPerson(db, e.personId);
    if (existing.some((note) => note.date === label)) continue;

    await createNote(db, {
      personId: e.personId,
      type: "1:1",
      date: label,
      summary: `Prep for 1:1 on ${label}.`,
    });
    created.add(key);
  }
}

// --- sync with network ----------------------------------------------------

async function defaultFetch(url) {
  return fetch(url, { redirect: "follow", signal: AbortSignal.timeout(10000) });
}

export async function syncFeed(db, feed, { fetchFn = defaultFetch, nowMs = Date.now() } = {}) {
  try {
    const res = await fetchFn(feed.url);
    if (!res.ok) throw new Error(`Feed responded ${res.status}`);
    const text = await res.text();
    return await applyFeedEvents(db, feed, text, nowMs);
  } catch (err) {
    calendarRepo.setFeedStatus(db, feed.id, {
      lastSyncedAt: feed.lastSyncedAt,
      lastError: String(err?.message ?? err),
    });
    return { error: String(err?.message ?? err) };
  }
}

export async function syncAll(db, opts = {}) {
  const feeds = calendarRepo.listFeeds(db);
  for (const feed of feeds) await syncFeed(db, feed, opts);
  return { feeds: feeds.length };
}

// --- read -----------------------------------------------------------------

export function getAgenda(db, { days = 14, nowMs = Date.now() } = {}) {
  const from = new Date(nowMs).toISOString();
  const to = new Date(nowMs + days * DAY).toISOString();
  return calendarRepo.listEventsBetween(db, from, to);
}

export function hasFeeds(db) {
  return calendarRepo.listFeeds(db).length > 0;
}
