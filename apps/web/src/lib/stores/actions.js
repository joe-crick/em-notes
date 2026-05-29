import { writable } from "svelte/store";
import * as actionsApi from "../api/actions-api.js";

// All action items across the team (the Actions board + Home open-actions list read this).
export const actions = writable([]);

export async function loadActions() {
  const res = await actionsApi.listActions();
  if (res.ok) actions.set(res.data);
  return res;
}

// Optimistically flip `done`, then persist. Reverts on failure.
export async function toggleAction(id) {
  let next;
  actions.update((list) =>
    list.map((a) => {
      if (a.id !== id) return a;
      next = !a.done;
      return { ...a, done: next };
    })
  );
  const res = await actionsApi.updateAction(id, { done: next });
  if (!res.ok) {
    actions.update((list) => list.map((a) => (a.id === id ? { ...a, done: !next } : a)));
  }
  return res;
}

export async function addAction(action) {
  const res = await actionsApi.createAction(action);
  if (res.ok) actions.update((list) => [res.data, ...list]);
  return res;
}

export function resetActions() {
  actions.set([]);
}
