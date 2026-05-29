import { conform, explain } from "@em-notes/contracts";

// The single place that turns an ljspec spec + request body into a validation result.
// Rule (§7): do not scatter raw `::invalid` checks outside this module.
export function validateBody(spec, body) {
  const value = conform(spec, body);
  if (value === "::invalid") {
    return {
      ok: false,
      error: {
        code: "invalid_request",
        message: explain(spec, body),
      },
    };
  }
  return { ok: true, value };
}
