<script>
  import { createPerson } from "../../lib/stores/people.js";
  import { closeAddReport } from "../../lib/stores/ui.js";
  import { goTo } from "../../lib/stores/route.js";
  import Icon from "../atoms/Icon.svelte";

  // Add a direct report (ported from screens-home-team.jsx). The prototype's mocked directory
  // and invite-by-email flows are fabricated, so per plan §1.1 they're shown as unavailable in
  // this local build; "Start blank" posts a real person to POST /api/people.
  let step = $state("source"); // source | form | success
  let busy = $state(false);
  let error = $state("");

  let draft = $state({
    name: "",
    role: "",
    level: "L4",
    timezone: "PST",
    startDate: "",
    reason: "new-hire",
    tags: "",
    scheduleFirst: true,
    sendWelcome: true,
  });

  const reasons = [
    ["new-hire", "New hire"],
    ["transfer", "Internal transfer"],
    ["return", "Returning"],
    ["contractor", "Contractor"],
  ];

  async function save() {
    if (!draft.name || !draft.role || busy) return;
    busy = true;
    error = "";
    const tags = draft.tags
      .split(",")
      .map((t) => t.trim())
      .filter(Boolean);
    const res = await createPerson({
      name: draft.name,
      role: draft.role,
      level: draft.level,
      timezone: draft.timezone,
      tags,
      flags: draft.reason === "new-hire" ? ["new-hire"] : [],
    });
    busy = false;
    if (res.ok) step = "success";
    else error = res.error?.message ?? "Could not add report.";
  }

  function reset() {
    draft = { ...draft, name: "", role: "", tags: "" };
    step = "source";
  }
</script>

<div
  class="modal-scrim"
  onclick={(e) => e.target === e.currentTarget && closeAddReport()}
  role="presentation"
>
  <div
    class="modal"
    style="width:620px; max-height:90vh; display:flex; flex-direction:column;"
    role="dialog"
    aria-modal="true"
    tabindex="-1"
  >
    <!-- Header -->
    <div class="between" style="padding:18px 22px; border-bottom:1px solid var(--line);">
      <div>
        <div class="eyebrow">{step === "success" ? "Done" : "Add direct report"}</div>
        <div class="display" style="font-size:22px; margin-top:2px;">
          {#if step === "source"}Who's joining your team?{/if}
          {#if step === "form"}{draft.name || "New direct report"}{/if}
          {#if step === "success"}{draft.name || "Your new report"} is set up{/if}
        </div>
      </div>
      <button class="btn-icon" onclick={closeAddReport} title="Close (Esc)" style="font-size:18px;">×</button>
    </div>

    <!-- Body -->
    <div style="padding:22px; overflow-y:auto; flex:1;">
      {#if step === "source"}
        <div class="stack">
          <div class="meta" style="margin-bottom:4px;">Choose a starting point.</div>

          <button
            class="card hover"
            style="padding:16px; width:100%; text-align:left; background:var(--bg-surface); cursor:pointer;"
            onclick={() => (step = "form")}
          >
            <div class="row" style="gap:14px;">
              <div
                style="width:36px; height:36px; border-radius:var(--radius-input); background:var(--accent-soft); color:var(--accent); display:flex; align-items:center; justify-content:center;"
              >
                <Icon name="plus" size={18} />
              </div>
              <div style="flex:1;">
                <div style="font-weight:600; font-size:15px;">Start blank</div>
                <div class="meta">Fill in details manually.</div>
              </div>
              <Icon name="chevright" size={16} color="var(--fg-4)" />
            </div>
          </button>

          <div
            class="row"
            style="gap:12px; margin-top:4px; padding:12px 14px; background:var(--bg-surface-2); border-radius:var(--radius-input);"
          >
            <Icon name="link" size={16} color="var(--fg-3)" />
            <div style="flex:1; font-size:13px; color:var(--fg-2);">
              Directory sync and invite-by-email aren't available in this local build.
            </div>
          </div>
        </div>
      {/if}

      {#if step === "form"}
        <div class="stack">
          <div>
            <label class="eyebrow" for="ar-name" style="display:block; margin-bottom:6px;">Name</label>
            <input
              id="ar-name"
              class="input"
              bind:value={draft.name}
              placeholder="First Last"
            />
          </div>

          <div class="row" style="gap:12px; align-items:stretch;">
            <div style="flex:2;">
              <label class="eyebrow" for="ar-role" style="display:block; margin-bottom:6px;">Role</label>
              <input id="ar-role" class="input" bind:value={draft.role} placeholder="Senior Engineer" />
            </div>
            <div style="flex:1;">
              <label class="eyebrow" for="ar-level" style="display:block; margin-bottom:6px;">Level</label>
              <select id="ar-level" class="input" bind:value={draft.level}>
                {#each ["L3", "L4", "L5", "L6", "L7"] as l}
                  <option>{l}</option>
                {/each}
              </select>
            </div>
            <div style="flex:1;">
              <label class="eyebrow" for="ar-tz" style="display:block; margin-bottom:6px;">Timezone</label>
              <select id="ar-tz" class="input" bind:value={draft.timezone}>
                {#each ["PST", "EST", "GMT", "CET", "IST", "JST"] as t}
                  <option>{t}</option>
                {/each}
              </select>
            </div>
          </div>

          <div>
            <span class="eyebrow" style="display:block; margin-bottom:6px;">Joining as</span>
            <div class="row" style="gap:4px; flex-wrap:wrap;">
              {#each reasons as [k, label] (k)}
                <button
                  class="btn btn-sm {draft.reason === k ? 'btn-primary' : 'btn-outline'}"
                  onclick={() => (draft.reason = k)}
                >
                  {label}
                </button>
              {/each}
            </div>
          </div>

          <div>
            <label class="eyebrow" for="ar-tags" style="display:block; margin-bottom:6px;">Tags</label>
            <input id="ar-tags" class="input" bind:value={draft.tags} placeholder="payments, promo-candidate" />
            <div class="meta" style="margin-top:4px; font-size:12px;">
              Comma-separated. Used for filtering and search.
            </div>
          </div>

          {#if error}
            <div class="meta" style="color:var(--status-err);">{error}</div>
          {/if}
        </div>
      {/if}

      {#if step === "success"}
        <div style="text-align:center; padding:20px 10px 8px;">
          <div
            style="width:64px; height:64px; margin:0 auto 16px; border-radius:999px; background:color-mix(in srgb, var(--status-ok) 18%, transparent); color:var(--status-ok); display:flex; align-items:center; justify-content:center;"
          >
            <Icon name="check" size={28} />
          </div>
          <div class="display" style="font-size:22px; margin-bottom:6px;">
            {draft.name} added to your team.
          </div>
          <div class="meta" style="margin-bottom:22px;">They now appear in your roster and sidebar.</div>
          <div class="row" style="gap:8px; justify-content:center;">
            <button class="btn btn-outline btn-sm" onclick={reset}>Add another</button>
            <button class="btn btn-primary btn-sm" onclick={closeAddReport}>Done</button>
          </div>
        </div>
      {/if}
    </div>

    <!-- Footer -->
    {#if step !== "success"}
      <div
        class="between"
        style="padding:14px 22px; border-top:1px solid var(--line); background:var(--bg-surface);"
      >
        <div class="meta" style="font-size:12px;">All fields except name can be edited later.</div>
        <div class="row" style="gap:8px;">
          {#if step === "form"}
            <button class="btn btn-ghost btn-sm" onclick={() => (step = "source")}>← Back</button>
          {/if}
          <button class="btn btn-ghost btn-sm" onclick={closeAddReport}>Cancel</button>
          {#if step === "form"}
            <button
              class="btn btn-accent btn-sm"
              disabled={!draft.name || !draft.role || busy}
              style="opacity:{draft.name && draft.role ? 1 : 0.5};"
              onclick={save}
            >
              {busy ? "Adding…" : "Add to team"}
            </button>
          {/if}
        </div>
      </div>
    {/if}
  </div>
</div>
