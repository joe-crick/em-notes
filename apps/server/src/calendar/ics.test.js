import { describe, it, expect } from "vitest";
import { parseIcs, eventsInWindow } from "./ics.js";

// Wrap a body of VEVENT lines in a minimal VCALENDAR envelope.
function ics(...lines) {
  return ["BEGIN:VCALENDAR", "VERSION:2.0", ...lines, "END:VCALENDAR"].join("\r\n");
}

function vevent(...lines) {
  return ics("BEGIN:VEVENT", ...lines, "END:VEVENT");
}

describe("parseIcs", () => {
  it("parses a single VEVENT's fields and a CN/mailto attendee (email lowercased)", () => {
    const text = vevent(
      "UID:evt-1@example.com",
      "SUMMARY:Weekly sync",
      "LOCATION:Room 4",
      "DTSTART:20260601T100000Z",
      "DTEND:20260601T103000Z",
      "ATTENDEE;CN=Alex Park:mailto:Alex@Example.com"
    );
    const events = parseIcs(text);
    expect(events).toHaveLength(1);
    const ev = events[0];
    expect(ev.uid).toBe("evt-1@example.com");
    expect(ev.summary).toBe("Weekly sync");
    expect(ev.location).toBe("Room 4");
    expect(ev.start.ms).toBe(Date.UTC(2026, 5, 1, 10, 0, 0));
    expect(ev.start.allDay).toBe(false);
    expect(ev.end.ms).toBe(Date.UTC(2026, 5, 1, 10, 30, 0));
    expect(ev.attendees).toEqual([{ email: "alex@example.com", name: "Alex Park" }]);
  });

  it("unfolds a SUMMARY folded across two lines (continuation starts with a space)", () => {
    const text = vevent(
      "UID:fold-1",
      "DTSTART:20260601T100000Z",
      "SUMMARY:Quarterly planning ",
      " and roadmap review"
    );
    const events = parseIcs(text);
    expect(events[0].summary).toBe("Quarterly planning and roadmap review");
  });

  it("parses an all-day event (DTSTART;VALUE=DATE) with allDay=true", () => {
    const text = vevent("UID:allday-1", "SUMMARY:Company holiday", "DTSTART;VALUE=DATE:20260601");
    const ev = parseIcs(text)[0];
    expect(ev.start.allDay).toBe(true);
    expect(ev.start.ms).toBe(Date.UTC(2026, 5, 1, 0, 0, 0));
  });
});

describe("eventsInWindow", () => {
  it("includes a non-recurring event only when its start is inside [from, to]", () => {
    const text = vevent("UID:single-1", "SUMMARY:One off", "DTSTART:20260601T100000Z");
    const start = Date.UTC(2026, 5, 1, 10, 0, 0);

    const inside = eventsInWindow(text, start - 3600000, start + 3600000);
    expect(inside).toHaveLength(1);
    expect(inside[0].uid).toBe("single-1");

    const before = eventsInWindow(text, start + 3600000, start + 7200000);
    expect(before).toHaveLength(0);

    const after = eventsInWindow(text, start - 7200000, start - 3600000);
    expect(after).toHaveLength(0);
  });

  it("expands a WEEKLY event into the right number of weekday instances, sorted ascending", () => {
    // 2026-06-01 is a Monday. WEEKLY on Mondays.
    const text = vevent(
      "UID:weekly-1",
      "SUMMARY:1:1",
      "DTSTART:20260601T100000Z",
      "RRULE:FREQ=WEEKLY;BYDAY=MO"
    );
    // Window of exactly four Mondays: Jun 1, 8, 15, 22 (Jun 29 is just outside `to`).
    const from = Date.UTC(2026, 5, 1, 0, 0, 0);
    const to = Date.UTC(2026, 5, 28, 23, 59, 59);

    const out = eventsInWindow(text, from, to);
    expect(out).toHaveLength(4);
    expect(out.map((e) => e.startsAt)).toEqual([
      new Date(Date.UTC(2026, 5, 1, 10, 0, 0)).toISOString(),
      new Date(Date.UTC(2026, 5, 8, 10, 0, 0)).toISOString(),
      new Date(Date.UTC(2026, 5, 15, 10, 0, 0)).toISOString(),
      new Date(Date.UTC(2026, 5, 22, 10, 0, 0)).toISOString(),
    ]);
    // Every instance lands on a Monday (UTC day-of-week 1).
    for (const e of out) {
      expect(new Date(e.startsAt).getUTCDay()).toBe(1);
    }
    // Sorted ascending.
    const isoSorted = [...out].map((e) => e.startsAt).sort();
    expect(out.map((e) => e.startsAt)).toEqual(isoSorted);
  });

  it("includes an all-day event whose date falls within the window", () => {
    const text = vevent("UID:allday-2", "SUMMARY:Offsite", "DTSTART;VALUE=DATE:20260601");
    const dayMs = Date.UTC(2026, 5, 1, 0, 0, 0);
    const out = eventsInWindow(text, dayMs - 86400000, dayMs + 86400000);
    expect(out).toHaveLength(1);
    expect(out[0].uid).toBe("allday-2");
    expect(out[0].startsAt).toBe(new Date(dayMs).toISOString());
  });
});
