import { sdef, shape_, enum_ } from "ljspec";
import { optional_ } from "./helpers.js";

// User settings. The prototype tweak panel is NOT ported (plan §1.1, §13.1); real settings
// are minimal. All fields optional; extra keys ride along via `shape_`.
export const UserSettings = sdef(
  "UserSettings",
  shape_({
    theme: optional_(enum_("light", "dark")),
    density: optional_(enum_("comfortable", "compact")),
  })
);
