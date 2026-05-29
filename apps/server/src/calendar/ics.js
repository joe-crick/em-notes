// Minimal, dependency-free iCalendar (.ics) reader for the calendar-feed sync.
//
// Scope (documented in docs/deviations.md): parses VEVENT SUMMARY / LOCATION / UID / DTSTART /
// DTEND / ATTENDEE / RRULE, and expands recurring events within a window. Times are handled as
// wall-clock — `Z` is true UTC; floating/`TZID` values are treated as UTC (their wall-clock time
// is preserved for display, but not converted between zones). RRULE support covers the common
// 1:1 cases: FREQ=DAILY|WEEKLY|MONTHLY with INTERVAL, COUNT, UNTIL, and (for WEEKLY) BYDAY.

const DAY = 86400000;
const DOW = ["SU", "MO", "TU", "WE", "TH", "FR", "SA"];

// --- line handling --------------------------------------------------------

// Unfold RFC 5545 line continuations (a line beginning with space/tab continues the previous).
function unfold(text) {
  return text
    .replace(/\r\n/g, "\n")
    .replace(/\r/g, "\n")
    .replace(/\n[ \t]/g, "");
}

function unescapeText(value) {
  return value
    .replace(/\\n/gi, "\n")
    .replace(/\\,/g, ",")
    .replace(/\\;/g, ";")
    .replace(/\\\\/g, "\\");
}

// Split "NAME;PARAM=x;PARAM2=y:VALUE" into { name, params, value }.
function parseLine(line) {
  const colon = line.indexOf(":");
  if (colon === -1) return null;
  const head = line.slice(0, colon);
  const value = line.slice(colon + 1);
  const [name, ...paramParts] = head.split(";");
  const params = {};
  for (const p of paramParts) {
    const eq = p.indexOf("=");
    if (eq !== -1) params[p.slice(0, eq).toUpperCase()] = p.slice(eq + 1);
  }
  return { name: name.toUpperCase(), params, value };
}

// --- date parsing ---------------------------------------------------------

// Parse an ical date/date-time to { ms, allDay }. Wall-clock is interpreted as UTC (see scope).
function parseDate(value, params = {}) {
  const allDay = params.VALUE === "DATE" || /^\d{8}$/.test(value);
  const m = value.match(/^(\d{4})(\d{2})(\d{2})(?:T(\d{2})(\d{2})(\d{2}))?/);
  if (!m) return null;
  const [, y, mo, d, h = "0", mi = "0", s = "0"] = m;
  const ms = Date.UTC(+y, +mo - 1, +d, +h, +mi, +s);
  return { ms, allDay };
}

function parseRrule(value) {
  const parts = Object.fromEntries(
    value.split(";").map((kv) => {
      const eq = kv.indexOf("=");
      return [kv.slice(0, eq).toUpperCase(), kv.slice(eq + 1)];
    })
  );
  return {
    freq: (parts.FREQ || "").toUpperCase(),
    interval: parts.INTERVAL ? Math.max(1, parseInt(parts.INTERVAL, 10)) : 1,
    count: parts.COUNT ? parseInt(parts.COUNT, 10) : null,
    until: parts.UNTIL ? parseDate(parts.UNTIL).ms : null,
    byday: parts.BYDAY ? parts.BYDAY.split(",").map((d) => d.trim().slice(-2).toUpperCase()) : null,
  };
}

// --- VEVENT parsing -------------------------------------------------------

export function parseIcs(text) {
  const lines = unfold(String(text || "")).split("\n");
  const events = [];
  let cur = null;

  for (const raw of lines) {
    const line = parseLine(raw);
    if (!line) continue;
    if (line.name === "BEGIN" && line.value === "VEVENT") {
      cur = { attendees: [] };
      continue;
    }
    if (line.name === "END" && line.value === "VEVENT") {
      if (cur && cur.start) events.push(cur);
      cur = null;
      continue;
    }
    if (!cur) continue;

    switch (line.name) {
      case "UID":
        cur.uid = line.value;
        break;
      case "SUMMARY":
        cur.summary = unescapeText(line.value);
        break;
      case "LOCATION":
        cur.location = unescapeText(line.value);
        break;
      case "DTSTART":
        cur.start = parseDate(line.value, line.params);
        break;
      case "DTEND":
        cur.end = parseDate(line.value, line.params);
        break;
      case "RRULE":
        cur.rrule = parseRrule(line.value);
        break;
      case "ATTENDEE": {
        const email = (line.value.match(/mailto:(.+)/i)?.[1] || "").trim().toLowerCase();
        const name = line.params.CN ? unescapeText(line.params.CN) : null;
        if (email || name) cur.attendees.push({ email, name });
        break;
      }
      default:
        break;
    }
  }
  return events;
}

// --- expansion ------------------------------------------------------------

function instance(ev, startMs, endMs) {
  return {
    uid: ev.uid ?? null,
    summary: ev.summary ?? "",
    location: ev.location ?? null,
    attendees: ev.attendees ?? [],
    startsAt: new Date(startMs).toISOString(),
    endsAt: endMs != null ? new Date(endMs).toISOString() : null,
  };
}

function expandOne(ev, fromMs, toMs, cap = 750) {
  const out = [];
  const defaultDur = ev.start.allDay ? DAY : 30 * 60000;
  const durMs = (ev.end ? ev.end.ms : ev.start.ms + defaultDur) - ev.start.ms;
  const push = (ms) => {
    if (ms >= fromMs && ms <= toMs) out.push(instance(ev, ms, ms + durMs));
  };

  if (!ev.rrule || !ev.rrule.freq) {
    push(ev.start.ms);
    return out;
  }

  const { freq, interval, count, until, byday } = ev.rrule;
  const untilMs = until ?? Infinity;
  const limit = count ?? Infinity;
  let emitted = 0;

  if (freq === "WEEKLY") {
    const start = new Date(ev.start.ms);
    const dayStart = Date.UTC(start.getUTCFullYear(), start.getUTCMonth(), start.getUTCDate());
    const tod = ev.start.ms - dayStart; // preserve time-of-day
    const sundayMidnight = dayStart - start.getUTCDay() * DAY;
    const dayNums = (byday?.length ? byday : [DOW[start.getUTCDay()]])
      .map((d) => DOW.indexOf(d))
      .filter((n) => n >= 0)
      .sort((a, b) => a - b);

    for (let w = 0; w < cap && emitted < limit; w++) {
      const weekBase = sundayMidnight + w * interval * 7 * DAY;
      if (weekBase > toMs) break;
      for (const dn of dayNums) {
        const occ = weekBase + dn * DAY + tod;
        if (occ < ev.start.ms || occ > untilMs) continue;
        emitted++;
        if (emitted > limit) break;
        push(occ);
        if (count && emitted >= count) break;
      }
    }
    return out;
  }

  // DAILY / MONTHLY / WEEKLY-without-byday: step from the series start.
  let ms = ev.start.ms;
  for (let i = 0; i < cap && emitted < limit; i++) {
    if (ms > untilMs || ms > toMs) break;
    push(ms);
    emitted++;
    if (count && emitted >= count) break;
    if (freq === "MONTHLY") {
      const d = new Date(ms);
      ms = Date.UTC(
        d.getUTCFullYear(),
        d.getUTCMonth() + interval,
        d.getUTCDate(),
        d.getUTCHours(),
        d.getUTCMinutes(),
        d.getUTCSeconds()
      );
    } else {
      ms += (freq === "DAILY" ? interval : interval * 7) * DAY;
    }
  }
  return out;
}

// Parse + expand a feed body into event instances whose start falls within [fromMs, toMs],
// sorted by start time.
export function eventsInWindow(text, fromMs, toMs) {
  return parseIcs(text)
    .flatMap((ev) => expandOne(ev, fromMs, toMs))
    .sort((a, b) => a.startsAt.localeCompare(b.startsAt));
}
