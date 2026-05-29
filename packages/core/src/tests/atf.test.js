import { describe, it, expect } from "vitest";
import { atf } from "../atf.js";

describe("atf", () => {
  it("returns the input unchanged when given no functions", async () => {
    expect(await atf(5)).toBe(5);
  });

  it("threads the value through synchronous functions left to right", async () => {
    const result = await atf(
      2,
      (n) => n + 1,
      (n) => n * 10
    );
    expect(result).toBe(30);
  });

  it("awaits asynchronous functions in the pipeline", async () => {
    const result = await atf(
      "a",
      async (s) => s + "b",
      (s) => s + "c",
      async (s) => s + "d"
    );
    expect(result).toBe("abcd");
  });

  it("preserves order when mixing sync and async steps", async () => {
    const trace = [];
    await atf(
      0,
      (n) => {
        trace.push("sync1");
        return n;
      },
      async (n) => {
        trace.push("async");
        return n;
      },
      (n) => {
        trace.push("sync2");
        return n;
      }
    );
    expect(trace).toEqual(["sync1", "async", "sync2"]);
  });
});
