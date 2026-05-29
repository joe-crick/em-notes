import { apiFetch } from "./client.js";

export const listFeeds = () => apiFetch("/calendar/feeds");

export const addFeed = (url, label) =>
  apiFetch("/calendar/feeds", { method: "POST", body: JSON.stringify({ url, label }) });

export const removeFeed = (id) => apiFetch(`/calendar/feeds/${id}`, { method: "DELETE" });

export const syncFeeds = () => apiFetch("/calendar/sync", { method: "POST" });

export const getAgenda = (days = 14) => apiFetch(`/calendar/agenda?days=${days}`);
