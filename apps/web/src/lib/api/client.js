// All frontend API calls go through here: relative `/api` URLs + cookie credentials, so the
// Vite dev proxy (and later Fastify serving the built assets) handles routing (plan §5.2).
export async function apiFetch(path, options = {}) {
  const headers = { ...(options.headers || {}) };
  // Only declare a JSON content-type when we're actually sending a body. A bodyless POST
  // (e.g. logout) sent with Content-Type: application/json is rejected by Fastify as an empty
  // body. (The plan's §5.2 snippet always sets it — see docs/deviations.md.)
  if (options.body != null && headers["Content-Type"] == null) {
    headers["Content-Type"] = "application/json";
  }

  let response;
  try {
    response = await fetch(`/api${path}`, { credentials: "include", ...options, headers });
  } catch {
    // Transport failure (server down, proxy error). Surface a structured envelope so callers can
    // branch on `res.ok` instead of catching exceptions everywhere.
    return {
      ok: false,
      error: { code: "network_error", message: "Could not reach the local server." },
    };
  }

  // The API always replies with a JSON envelope (even for 4xx/5xx). Anything else (HTML error
  // page, empty body) is normalized to a structured error rather than throwing.
  const payload = await response.json().catch(() => null);
  if (payload == null) {
    return { ok: false, error: { code: "bad_response", message: "Unexpected server response." } };
  }
  return payload;
}
