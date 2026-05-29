<script>
  import { updatePerson } from "../../lib/stores/people.js";
  import { closeEditPerson } from "../../lib/stores/ui.js";
  import Avatar from "../atoms/Avatar.svelte";

  // Edit an existing direct report (PATCH /api/people/:id). Deleting lives on the Person view,
  // not here.
  let { person } = $props();

  const LEVELS = ["L3", "L4", "L5", "L6", "L7"];
  const TIMEZONES = ["PST", "EST", "GMT", "CET", "IST", "JST"];
  const FLAGS = [
    ["sentiment-drop", "Sentiment ↓"],
    ["promotion-ready", "Promo-ready"],
    ["new-hire", "New hire"],
    ["no-note-7d", "7d gap"],
  ];

  // Seed the form from the person. growthProgress is 0–1 in the model; edit it as a percentage.
  let draft = $state({
    name: person.name ?? "",
    role: person.role ?? "",
    email: person.email ?? "",
    level: LEVELS.includes(person.level) ? person.level : LEVELS[1],
    timezone: TIMEZONES.includes(person.timezone) ? person.timezone : TIMEZONES[0],
    tenure: person.tenure ?? "",
    pronouns: person.pronouns ?? "",
    nextOneOnOne: person.nextOneOnOne ?? "",
    growthFocus: person.growthFocus ?? "",
    growthPercent: Math.round((person.growthProgress ?? 0) * 100),
    tags: (person.tags ?? []).join(", "),
    flags: [...(person.flags ?? [])],
  });

  let busy = $state(false);
  let error = $state("");

  function toggleFlag(kind) {
    draft.flags = draft.flags.includes(kind)
      ? draft.flags.filter((f) => f !== kind)
      : [...draft.flags, kind];
  }

  async function save() {
    if (!draft.name.trim() || !draft.role.trim() || busy) return;
    busy = true;
    error = "";
    const pct = Math.max(0, Math.min(100, Number(draft.growthPercent) || 0));
    const res = await updatePerson(person.id, {
      name: draft.name.trim(),
      role: draft.role.trim(),
      email: draft.email.trim() || null,
      level: draft.level,
      timezone: draft.timezone,
      tenure: draft.tenure.trim() || null,
      pronouns: draft.pronouns.trim() || null,
      nextOneOnOne: draft.nextOneOnOne.trim() || null,
      growthFocus: draft.growthFocus.trim() || null,
      growthProgress: pct / 100,
      tags: draft.tags
        .split(",")
        .map((t) => t.trim())
        .filter(Boolean),
      flags: draft.flags,
    });
    busy = false;
    if (res.ok) closeEditPerson();
    else error = res.error?.message ?? "Could not save changes.";
  }

</script>

<div
  class="modal-scrim"
  onclick={(e) => e.target === e.currentTarget && closeEditPerson()}
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
      <div class="row" style="gap:14px;">
        <Avatar {person} size="md" />
        <div>
          <div class="eyebrow">Edit report</div>
          <div class="display" style="font-size:22px;">{draft.name || person.name}</div>
        </div>
      </div>
      <button class="btn-icon" onclick={closeEditPerson} title="Close (Esc)" style="font-size:18px;">×</button>
    </div>

    <!-- Body -->
    <div style="padding:22px; overflow-y:auto; flex:1;">
      <div class="stack">
        <div>
          <label class="eyebrow" for="ep-name" style="display:block; margin-bottom:6px;">Name</label>
          <input id="ep-name" class="input" bind:value={draft.name} placeholder="First Last" />
        </div>

        <div class="row" style="gap:12px; align-items:stretch;">
          <div style="flex:2;">
            <label class="eyebrow" for="ep-role" style="display:block; margin-bottom:6px;">Role</label>
            <input id="ep-role" class="input" bind:value={draft.role} placeholder="Senior Engineer" />
          </div>
          <div style="flex:1;">
            <label class="eyebrow" for="ep-level" style="display:block; margin-bottom:6px;">Level</label>
            <select id="ep-level" class="input" bind:value={draft.level}>
              {#each LEVELS as l}<option>{l}</option>{/each}
            </select>
          </div>
          <div style="flex:1;">
            <label class="eyebrow" for="ep-tz" style="display:block; margin-bottom:6px;">Timezone</label>
            <select id="ep-tz" class="input" bind:value={draft.timezone}>
              {#each TIMEZONES as t}<option>{t}</option>{/each}
            </select>
          </div>
        </div>

        <div>
          <label class="eyebrow" for="ep-email" style="display:block; margin-bottom:6px;">Work email</label>
          <input id="ep-email" class="input" type="email" bind:value={draft.email} placeholder="name@company.com" />
          <div class="meta" style="margin-top:4px; font-size:12px;">Used to match calendar invites to this report.</div>
        </div>

        <div class="row" style="gap:12px; align-items:stretch;">
          <div style="flex:1;">
            <label class="eyebrow" for="ep-tenure" style="display:block; margin-bottom:6px;">Tenure</label>
            <input id="ep-tenure" class="input" bind:value={draft.tenure} placeholder="2y 4mo" />
          </div>
          <div style="flex:1;">
            <label class="eyebrow" for="ep-pronouns" style="display:block; margin-bottom:6px;">Pronouns</label>
            <input id="ep-pronouns" class="input" bind:value={draft.pronouns} placeholder="she/her" />
          </div>
          <div style="flex:1;">
            <label class="eyebrow" for="ep-next" style="display:block; margin-bottom:6px;">Next 1:1</label>
            <input id="ep-next" class="input" bind:value={draft.nextOneOnOne} placeholder="Tue 10:00" />
          </div>
        </div>

        <div class="row" style="gap:12px; align-items:stretch;">
          <div style="flex:2;">
            <label class="eyebrow" for="ep-growth" style="display:block; margin-bottom:6px;">Growth focus</label>
            <input id="ep-growth" class="input" bind:value={draft.growthFocus} placeholder="Tech lead readiness" />
          </div>
          <div style="flex:1;">
            <label class="eyebrow" for="ep-pct" style="display:block; margin-bottom:6px;">Growth %</label>
            <input id="ep-pct" class="input" type="number" min="0" max="100" bind:value={draft.growthPercent} />
          </div>
        </div>

        <div>
          <label class="eyebrow" for="ep-tags" style="display:block; margin-bottom:6px;">Tags</label>
          <input id="ep-tags" class="input" bind:value={draft.tags} placeholder="payments, promo-candidate" />
          <div class="meta" style="margin-top:4px; font-size:12px;">Comma-separated.</div>
        </div>

        <div>
          <span class="eyebrow" style="display:block; margin-bottom:6px;">Flags</span>
          <div class="row" style="gap:6px; flex-wrap:wrap;">
            {#each FLAGS as [kind, label] (kind)}
              <button
                class="btn btn-sm {draft.flags.includes(kind) ? 'btn-primary' : 'btn-outline'}"
                onclick={() => toggleFlag(kind)}
              >
                {label}
              </button>
            {/each}
          </div>
        </div>

        {#if error}
          <div class="meta" style="color:var(--status-err);">{error}</div>
        {/if}
      </div>
    </div>

    <!-- Footer -->
    <div class="between" style="padding:14px 22px; border-top:1px solid var(--line); background:var(--bg-surface);">
      <div class="meta" style="font-size:12px;">Changes save to your local database.</div>
      <div class="row" style="gap:8px;">
        <button class="btn btn-ghost btn-sm" onclick={closeEditPerson}>Cancel</button>
        <button
          class="btn btn-accent btn-sm"
          disabled={!draft.name.trim() || !draft.role.trim() || busy}
          onclick={save}
        >
          {busy ? "Saving…" : "Save changes"}
        </button>
      </div>
    </div>
  </div>
</div>
