import { writable } from "svelte/store";

// Lightweight route state matching the prototype (plan §12.1):
// name: "home" | "team" | "actions" | "person" | "settings", personId: string | null.
export const route = writable({ name: "home", personId: null });

export function goTo(name, personId = null) {
  route.set({ name, personId });
}
