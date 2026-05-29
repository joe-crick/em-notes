<script>
  import { actions as actionsStore, loadActions } from "../../lib/stores/actions.js";
  import { loadPeople } from "../../lib/stores/people.js";
  import { loadNotes } from "../../lib/stores/notes.js";
  import { closeNewNote } from "../../lib/stores/ui.js";
  import { goTo } from "../../lib/stores/route.js";
  import * as notesApi from "../../lib/api/notes-api.js";
  import Icon from "../atoms/Icon.svelte";
  import Avatar from "../atoms/Avatar.svelte";
  import Sparkline from "../atoms/Sparkline.svelte";
  import SentimentDot from "../atoms/SentimentDot.svelte";
  import Kbd from "../atoms/Kbd.svelte";

  // New 1:1 note (ported from screens-actions-note.jsx). Persists to POST /api/people/:id/notes.
  // Prep context comes from real data (the person's open actions + flags); the AI/transcription
  // affordances from the prototype are omitted per plan §1.1.
  let { person } = $props();

  let tab = $state("prep"); // prep | notes | actions | wrap
  let busy = $state(false);
  let error = $state("");

  let draft = $state({
    type: "1:1",
    date: new Date().toISOString().slice(0, 10),
    duration: 30,
    sentiment: 4,
    talkingPoints: [],
    discussion: "",
    actions: [],
  });
  let actionDraft = $state("");

  // The date input gives an ISO "YYYY-MM-DD"; store the human format the seed data uses
  // ("May 29, 2026") so notes read consistently. Parsed as local time to avoid TZ drift.
  function humanDate(iso) {
    const [y, m, d] = iso.split("-").map(Number);
    if (!y || !m || !d) return iso;
    return new Intl.DateTimeFormat("en-US", { month: "long", day: "numeric", year: "numeric" }).format(
      new Date(y, m - 1, d)
    );
  }

  // Carry-over: this person's still-open actions (real, from the actions store).
  const carryOver = $derived(
    $actionsStore.filter((a) => a.personId === person.id && !a.done).slice(0, 4)
  );

  const suggestedPoints = $derived.by(() => {
    const pts = carryOver.map((a) => ({ id: `co-${a.id}`, text: a.text, source: "Carry-over" }));
    const flags = person.flags ?? [];
    if (flags.includes("sentiment-drop"))
      pts.push({ id: "sd", text: "How have the last couple of weeks felt?", source: "Sentiment" });
    if (flags.includes("promotion-ready"))
      pts.push({ id: "pr", text: "Promo packet progress and next steps", source: "Promo" });
    return pts;
  });

  const lastScore = $derived(person.sentiment?.length ? person.sentiment[person.sentiment.length - 1] : null);

  function togglePoint(pt) {
    draft.talkingPoints = draft.talkingPoints.find((p) => p.id === pt.id)
      ? draft.talkingPoints.filter((p) => p.id !== pt.id)
      : [...draft.talkingPoints, pt];
  }

  function seedDiscussionFromPoints() {
    if (!draft.discussion && draft.talkingPoints.length) {
      draft.discussion = draft.talkingPoints.map((p) => `## ${p.text}\n\n`).join("\n");
    }
  }

  function addActionItem() {
    if (!actionDraft.trim()) return;
    draft.actions = [...draft.actions, { text: actionDraft.trim(), owner: "me", done: false }];
    actionDraft = "";
  }

  function removeAction(i) {
    draft.actions = draft.actions.filter((_, idx) => idx !== i);
  }

  function toggleOwner(i) {
    draft.actions = draft.actions.map((a, idx) =>
      idx === i ? { ...a, owner: a.owner === "me" ? "report" : "me" } : a
    );
  }

  const templates = [
    ["Standard 1:1", "Wins · Blockers · Feedback · Growth"],
    ["Career chat", "Aspirations · Strengths · Gaps · Plan"],
    ["Difficult convo", "Observation · Impact · Ask · Next"],
    ["Quarterly recap", "Goals · Highlights · Misses · Theme"],
  ];

  function applyTemplate(sub) {
    const sections = sub.split(" · ").map((s) => `## ${s}\n\n`).join("\n");
    draft.discussion = draft.discussion ? `${draft.discussion}\n${sections}` : sections;
    tab = "notes";
  }

  async function save() {
    if (busy) return;
    const summary = draft.discussion.trim();
    if (!summary) {
      error = "Add some discussion notes before saving.";
      tab = "notes";
      return;
    }
    busy = true;
    error = "";
    const res = await notesApi.createNote(person.id, {
      type: draft.type,
      date: humanDate(draft.date),
      duration: Number(draft.duration) || 0,
      sentiment: draft.sentiment,
      summary,
      highlights: draft.talkingPoints.map((p) => p.text),
      actions: draft.actions.map((a) => ({ text: a.text, owner: a.owner, done: a.done })),
    });
    busy = false;
    if (!res.ok) {
      error = res.error?.message ?? "Could not save the note.";
      return;
    }
    await Promise.all([loadActions(), loadPeople(), loadNotes(person.id)]);
    closeNewNote();
    goTo("person", person.id);
  }

  const tabList = $derived([
    ["prep", "Prep", suggestedPoints.length],
    ["notes", "Notes", null],
    ["actions", "Action items", draft.actions.length || null],
    ["wrap", "Wrap-up", null],
  ]);

  const showRail = $derived(tab === "notes" || tab === "wrap");

  const sentimentLabels = [
    [1, "Concerning"],
    [2, "Tough"],
    [3, "Neutral"],
    [4, "Good"],
    [5, "Energizing"],
  ];
</script>

<div
  class="modal-scrim"
  onclick={(e) => e.target === e.currentTarget && closeNewNote()}
  role="presentation"
>
  <div
    class="modal"
    style="width:94vw; max-width:1080px; height:92vh; display:flex; flex-direction:column;"
    role="dialog"
    aria-modal="true"
    tabindex="-1"
  >
    <!-- Header -->
    <div class="between" style="padding:16px 24px; border-bottom:1px solid var(--line);">
      <div class="row" style="gap:14px;">
        <Avatar {person} size="md" />
        <div>
          <div class="eyebrow">New {draft.type} note</div>
          <div class="display" style="font-size:22px;">{person.name}</div>
        </div>
        <div class="row" style="gap:6px; margin-left:16px;">
          <select class="input" bind:value={draft.type} style="width:auto; padding:6px 10px; font-size:13px;">
            <option>1:1</option>
            <option>Skip</option>
            <option>Career</option>
            <option>Retro</option>
            <option>Ad-hoc</option>
          </select>
          <input class="input" type="date" bind:value={draft.date} style="width:auto; padding:6px 10px; font-size:13px;" />
          <input class="input" type="number" bind:value={draft.duration} style="width:72px; padding:6px 10px; font-size:13px;" />
          <span class="meta" style="font-size:12px;">min</span>
        </div>
      </div>
      <div class="row" style="gap:8px;">
        <button class="btn btn-ghost btn-sm" onclick={closeNewNote}>Discard</button>
        <button class="btn btn-primary btn-sm" disabled={busy} onclick={save}>
          <Icon name="check" size={14} /> {busy ? "Saving…" : "Save note"}
        </button>
      </div>
    </div>

    <!-- Tabs -->
    <div style="padding:0 24px; border-bottom:1px solid var(--line);">
      <div class="tabs" style="border-bottom:none;">
        {#each tabList as [k, label, n] (k)}
          <button
            class="tab {tab === k ? 'active' : ''}"
            onclick={() => {
              tab = k;
              if (k === "notes") seedDiscussionFromPoints();
            }}
          >
            {label}{#if n != null}<span class="mono" style="margin-left:6px; color:var(--fg-4); font-size:12px;">{n}</span>{/if}
          </button>
        {/each}
      </div>
    </div>

    <!-- Body -->
    <div style="flex:1; overflow:hidden; display:grid; grid-template-columns:{showRail ? '1fr 320px' : '1fr'};">
      <div style="overflow-y:auto; padding:20px 24px;">
        {#if tab === "prep"}
          <div class="stack" style="max-width:720px;">
            <div>
              <div class="display" style="font-size:22px; margin-bottom:4px;">Build today's agenda</div>
              <div class="meta">Pick from surfaced context, or add your own. Selected items become highlights.</div>
            </div>

            <section>
              <div class="eyebrow" style="margin-bottom:8px;">Carry-over from last 1:1</div>
              {#if carryOver.length === 0}
                <div class="card card-pad meta">Nothing carried over.</div>
              {:else}
                <div class="card" style="padding:0;">
                  {#each carryOver as a (a.id)}
                    <div class="list-row" style="grid-template-columns:1fr auto;">
                      <div style="font-size:14px; color:var(--fg-1);">{a.text}</div>
                      <span class="chip chip-draft">{a.owner === "me" ? "You" : person.name.split(" ")[0]}</span>
                    </div>
                  {/each}
                </div>
              {/if}
            </section>

            <section>
              <div class="between" style="margin-bottom:8px;">
                <div class="eyebrow">Surfaced from context</div>
                <span class="meta" style="font-size:11px;">{draft.talkingPoints.length} selected</span>
              </div>
              {#if suggestedPoints.length === 0}
                <div class="card card-pad meta">No context to surface yet.</div>
              {:else}
                <div class="card" style="padding:0;">
                  {#each suggestedPoints as pt (pt.id)}
                    {@const on = !!draft.talkingPoints.find((p) => p.id === pt.id)}
                    <div
                      class="list-row"
                      style="grid-template-columns:auto 1fr auto; cursor:pointer;"
                      onclick={() => togglePoint(pt)}
                      onkeydown={(e) => e.key === "Enter" && togglePoint(pt)}
                      role="button"
                      tabindex="0"
                    >
                      <span
                        style="width:18px; height:18px; border-radius:4px; border:1.5px solid {on ? 'var(--accent)' : 'var(--line-strong)'}; background:{on ? 'var(--accent)' : 'transparent'}; display:flex; align-items:center; justify-content:center;"
                      >
                        {#if on}<Icon name="check" size={12} color="#fff" />{/if}
                      </span>
                      <div style="font-size:14px; color:var(--fg-1);">{pt.text}</div>
                      <span class="chip chip-draft" style="font-size:10px;">{pt.source}</span>
                    </div>
                  {/each}
                </div>
              {/if}
            </section>

            <section>
              <div class="eyebrow" style="margin-bottom:8px;">Insert a template</div>
              <div class="row" style="gap:8px; flex-wrap:wrap;">
                {#each templates as [name, sub] (name)}
                  <button
                    class="card hover"
                    style="padding:12px; text-align:left; cursor:pointer; min-width:200px;"
                    onclick={() => applyTemplate(sub)}
                  >
                    <div style="font-size:14px; font-weight:600;">{name}</div>
                    <div class="meta" style="font-size:12px;">{sub}</div>
                  </button>
                {/each}
              </div>
            </section>
          </div>
        {/if}

        {#if tab === "notes"}
          <div style="max-width:720px;">
            <div class="meta" style="margin-bottom:8px; font-size:12px;">
              Discussion notes — this becomes the note's summary.
            </div>
            <textarea
              class="textarea"
              bind:value={draft.discussion}
              placeholder="What did you talk about?"
              style="min-height:360px; font-size:15px; line-height:1.7; font-family:var(--font-ui);"
            ></textarea>
            {#if error}
              <div class="meta" style="color:var(--status-err); margin-top:10px;">{error}</div>
            {/if}
          </div>
        {/if}

        {#if tab === "actions"}
          <div style="max-width:720px;">
            <div class="display" style="font-size:22px; margin-bottom:4px;">Action items</div>
            <div class="meta" style="margin-bottom:16px;">Each item appears on the Actions board with the right owner.</div>

            <div class="row" style="gap:8px; margin-bottom:16px;">
              <input
                class="input"
                bind:value={actionDraft}
                onkeydown={(e) => e.key === "Enter" && addActionItem()}
                placeholder="Add an action item, e.g. 'Schedule pair on auth refactor'"
              />
              <button class="btn btn-accent" onclick={addActionItem}><Icon name="plus" size={14} /> Add</button>
            </div>

            {#if draft.actions.length === 0}
              <div class="card card-pad" style="text-align:center; padding:32px;">
                <div class="meta">No action items yet.</div>
              </div>
            {:else}
              <div class="card" style="padding:0;">
                {#each draft.actions as a, i (i)}
                  <div class="list-row" style="grid-template-columns:1fr auto auto; cursor:default;">
                    <div style="font-size:14px;">{a.text}</div>
                    <button class="chip" style="cursor:pointer;" onclick={() => toggleOwner(i)}>
                      {a.owner === "me" ? "You" : person.name.split(" ")[0]} ↔
                    </button>
                    <button class="btn-icon" onclick={() => removeAction(i)} title="Remove">
                      <Icon name="more" size={14} />
                    </button>
                  </div>
                {/each}
              </div>
            {/if}
          </div>
        {/if}

        {#if tab === "wrap"}
          <div class="stack" style="max-width:720px;">
            <div>
              <div class="display" style="font-size:22px; margin-bottom:4px;">Wrap-up</div>
              <div class="meta">A quick sentiment pulse helps surface trends over time.</div>
            </div>
            <section>
              <div class="eyebrow" style="margin-bottom:10px;">How did this conversation feel?</div>
              <div class="row" style="gap:6px;">
                {#each sentimentLabels as [score, label] (score)}
                  <button
                    class="card hover"
                    style="flex:1; padding:14px 8px; text-align:center; cursor:pointer; border-color:{draft.sentiment === score ? 'var(--accent)' : 'var(--line)'}; border-width:{draft.sentiment === score ? 2 : 1}px; background:{draft.sentiment === score ? 'var(--accent-soft)' : 'var(--bg-surface)'};"
                    onclick={() => (draft.sentiment = score)}
                  >
                    <div class="display" style="font-size:22px; color:{draft.sentiment === score ? 'var(--accent)' : 'var(--fg-2)'};">{score}</div>
                    <div class="meta" style="font-size:11px; margin-top:4px;">{label}</div>
                  </button>
                {/each}
              </div>
              <div class="meta" style="margin-top:10px; font-size:12px;">
                This signal is private to you and used only for sentiment trends.
              </div>
            </section>
            {#if error}
              <div class="meta" style="color:var(--status-err);">{error}</div>
            {/if}
          </div>
        {/if}
      </div>

      {#if showRail}
        <aside style="border-left:1px solid var(--line); padding:20px; overflow-y:auto; background:var(--bg-page);">
          <div class="stack" style="--stack:20px;">
            <div>
              <div class="eyebrow" style="margin-bottom:8px;">Talking points</div>
              {#if draft.talkingPoints.length === 0}
                <div class="meta" style="font-size:12px;">None selected. Pick some on the Prep tab.</div>
              {:else}
                <ul style="margin:0; padding-left:18px; color:var(--fg-2); font-size:13px; line-height:1.5;">
                  {#each draft.talkingPoints as p (p.id)}
                    <li>{p.text}</li>
                  {/each}
                </ul>
              {/if}
            </div>
            <div>
              <div class="eyebrow" style="margin-bottom:8px;">Sentiment · {person.sentiment?.length ?? 0} wks</div>
              <div class="card card-pad" style="padding:12px;">
                <div class="row" style="gap:10px; align-items:center;">
                  {#if lastScore != null}<SentimentDot score={lastScore} />{/if}
                  <Sparkline values={person.sentiment} width={120} height={28} accent={(person.flags ?? []).includes("sentiment-drop")} />
                </div>
                <div class="meta" style="margin-top:6px; font-size:12px;">{person.sentimentLabel}</div>
              </div>
            </div>
          </div>
        </aside>
      {/if}
    </div>

    <!-- Footer -->
    <div class="between" style="padding:12px 24px; border-top:1px solid var(--line); background:var(--bg-surface);">
      <div class="row" style="gap:14px; color:var(--fg-3); font-size:12px;">
        <span><Kbd>esc</Kbd> close</span>
      </div>
      <div class="row" style="gap:8px;">
        {#if tab !== "prep"}
          <button
            class="btn btn-ghost btn-sm"
            onclick={() => (tab = { notes: "prep", actions: "notes", wrap: "actions" }[tab])}
          >
            ← Back
          </button>
        {/if}
        {#if tab !== "wrap"}
          <button
            class="btn btn-outline btn-sm"
            onclick={() => {
              if (tab === "prep") seedDiscussionFromPoints();
              tab = { prep: "notes", notes: "actions", actions: "wrap" }[tab];
            }}
          >
            Continue →
          </button>
        {:else}
          <button class="btn btn-primary btn-sm" disabled={busy} onclick={save}>
            <Icon name="check" size={14} /> Save & close
          </button>
        {/if}
      </div>
    </div>
  </div>
</div>
