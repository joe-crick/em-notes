import { writable } from "svelte/store";
import * as peopleApi from "../api/people-api.js";

export const people = writable([]);

export async function loadPeople() {
  const res = await peopleApi.listPeople();
  if (res.ok) people.set(res.data);
  return res;
}

export async function createPerson(input) {
  const res = await peopleApi.createPerson(input);
  if (res.ok) await loadPeople();
  return res;
}

export function resetPeople() {
  people.set([]);
}
