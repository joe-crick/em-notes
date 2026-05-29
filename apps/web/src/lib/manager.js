// The signed-in manager. This is a single-user local app with no `/api/me` endpoint, so the
// identity is a fixed constant shared by the Topbar, Home greeting, and Settings profile
// (see docs/deviations.md §13). Mirrors the prototype `ME` and the seed author.
export const ME = {
  name: "Joe Crick",
  role: "Engineering Manager",
  team: "Payments Platform",
  initials: "JC",
  color: "var(--accent)",
};

export function greeting(date = new Date()) {
  const h = date.getHours();
  if (h < 12) return "Good morning";
  if (h < 18) return "Good afternoon";
  return "Good evening";
}
