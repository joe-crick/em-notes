import { get } from "svelte/store";
import { route, goTo } from "../stores/route.js";
import { people } from "../stores/people.js";
import { logout } from "../stores/session.js";
import {
  paletteOpen,
  togglePalette,
  openPalette,
  closePalette,
  newNotePerson,
  openNewNote,
  closeNewNote,
  addReportOpen,
  closeAddReport,
  editPerson,
  closeEditPerson,
  deletePersonTarget,
  closeDeletePerson,
} from "../stores/ui.js";

// Global keyboard shortcuts (plan §12.4). A single handler is attached to the window by App.
// `g` is a two-key leader (e.g. `g h` → home); a short timeout resets it.
let pendingG = false;
let gTimer = null;

function armG() {
  pendingG = true;
  clearTimeout(gTimer);
  gTimer = setTimeout(() => (pendingG = false), 800);
}

function isEditing(target) {
  const tag = (target?.tagName || "").toLowerCase();
  return tag === "input" || tag === "textarea" || tag === "select" || target?.isContentEditable;
}

// Open a new note for the routed person, or fall back to the palette to pick one.
function newNoteForCurrent() {
  const r = get(route);
  if (r.name === "person" && r.personId) {
    const person = get(people).find((p) => p.id === r.personId);
    if (person) {
      openNewNote(person);
      return;
    }
  }
  openPalette();
}

export function handleKeydown(e) {
  // Cmd/Ctrl+K toggles the palette from anywhere (even inside inputs).
  if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === "k") {
    e.preventDefault();
    togglePalette();
    return;
  }

  // Escape closes the topmost overlay.
  if (e.key === "Escape") {
    if (get(paletteOpen)) closePalette();
    else if (get(newNotePerson)) closeNewNote();
    else if (get(addReportOpen)) closeAddReport();
    else if (get(editPerson)) closeEditPerson();
    else if (get(deletePersonTarget)) closeDeletePerson();
    return;
  }

  // Everything below is disabled while typing or when a modal owns the focus.
  if (
    isEditing(e.target) ||
    get(newNotePerson) ||
    get(addReportOpen) ||
    get(editPerson) ||
    get(deletePersonTarget) ||
    get(paletteOpen)
  ) {
    return;
  }

  if (e.key === "/") {
    e.preventDefault();
    openPalette();
    return;
  }

  if (e.key.toLowerCase() === "n" && !pendingG) {
    e.preventDefault();
    newNoteForCurrent();
    return;
  }

  if (e.key.toLowerCase() === "g") {
    armG();
    return;
  }

  if (pendingG) {
    pendingG = false;
    const k = e.key.toLowerCase();
    if (k === "h") goTo("home");
    else if (k === "t") goTo("team");
    else if (k === "a") goTo("actions");
    else if (k === "s") goTo("settings");
    else if (/^[1-9]$/.test(e.key)) {
      const person = get(people)[Number(e.key) - 1];
      if (person) goTo("person", person.id);
    }
  }
}
