import { customAlphabet } from "nanoid";

// Alphanumeric only (no `_` or `-`) so `prefix_id` parses unambiguously.
const ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
const nano = customAlphabet(ALPHABET, 21);

// createId("note") -> "note_xY3...". createId() -> bare id with no prefix.
export function createId(prefix) {
  const id = nano();
  return prefix ? `${prefix}_${id}` : id;
}
