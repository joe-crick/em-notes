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

  const response = await fetch(`/api${path}`, {
    credentials: "include",
    ...options,
    headers,
  });

  return await response.json();
}
