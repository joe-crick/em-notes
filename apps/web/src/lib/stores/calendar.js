import { writable } from "svelte/store";
import * as calendarApi from "../api/calendar-api.js";

// Subscribed feeds + the upcoming agenda (matched event instances) they produce.
export const feeds = writable([]);
export const agenda = writable([]);

export async function loadFeeds() {
  const res = await calendarApi.listFeeds();
  if (res.ok) feeds.set(res.data);
  return res;
}

export async function loadAgenda() {
  const res = await calendarApi.getAgenda();
  if (res.ok) agenda.set(res.data);
  return res;
}

export async function addFeed(url, label) {
  const res = await calendarApi.addFeed(url, label);
  if (res.ok) await Promise.all([loadFeeds(), loadAgenda()]);
  return res;
}

export async function removeFeed(id) {
  const res = await calendarApi.removeFeed(id);
  if (res.ok) await Promise.all([loadFeeds(), loadAgenda()]);
  return res;
}

// Trigger a server-side refresh of every feed, then reload feeds + agenda.
export async function syncFeeds() {
  const res = await calendarApi.syncFeeds();
  await Promise.all([loadFeeds(), loadAgenda()]);
  return res;
}

export function resetCalendar() {
  feeds.set([]);
  agenda.set([]);
}
