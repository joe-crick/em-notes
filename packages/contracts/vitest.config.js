import { defineConfig } from "vitest/config";

// Domain spec files are named `*.spec.js` per the plan's layout (§3), which collides with
// vitest's default `*.spec.*` test-discovery glob. Restrict discovery to real test files so
// the spec sources aren't executed as (empty) test suites.
export default defineConfig({
  test: {
    include: ["src/**/*.test.js"],
  },
});
