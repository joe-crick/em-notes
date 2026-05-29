import { sdef, shape_, NonEmptyStr } from "ljspec";
import { optional_, minLenStr_ } from "./helpers.js";

// Single-user local auth (plan §10). Login only needs the password.
export const LoginInput = sdef(
  "LoginInput",
  shape_({
    password: NonEmptyStr,
  })
);

// First-run setup. Enforce a minimum length as a local password policy.
export const SetupPasswordInput = sdef(
  "SetupPasswordInput",
  shape_({
    password: minLenStr_(8),
  })
);

// Server-side session stored in SQLite.
export const Session = sdef(
  "Session",
  shape_({
    id: NonEmptyStr,
    userId: NonEmptyStr,
    expiresAt: NonEmptyStr,
    createdAt: optional_(NonEmptyStr),
  })
);
