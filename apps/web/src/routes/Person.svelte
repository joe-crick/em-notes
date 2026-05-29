<script>
  import { people } from "../lib/stores/people.js";
  import { actions, toggleAction } from "../lib/stores/actions.js";
  import { route, goTo } from "../lib/stores/route.js";
  import { openNewNote } from "../lib/stores/ui.js";
  import { notes as notesStore, loadNotes } from "../lib/stores/notes.js";
  import Icon from "../components/atoms/Icon.svelte";
  import Avatar from "../components/atoms/Avatar.svelte";
  import Sparkline from "../components/atoms/Sparkline.svelte";
  import Bar from "../components/atoms/Bar.svelte";
  import Flag from "../components/atoms/Flag.svelte";
  import Kbd from "../components/atoms/Kbd.svelte";
  import SentimentDot from "../components/atoms/SentimentDot.svelte";
  import AICard from "../components/atoms/AICard.svelte";
  import SectionHeader from "../components/SectionHeader.svelte";

  let tab = $state("overview");
  let selectedNoteId = $state(null);

  const notes = $derived($notesStore);
  const person = $derived($people.find((p) => p.id === $route.personId) ?? null);
  const personActions = $derived(
    person ? $actions.filter((a) => a.personId === person.id && !a.done) : []
  );
  const lastScore = $derived(person?.sentiment?.length ? person.sentiment[person.sentiment.length - 1] : null);

  // Load this person's notes whenever the routed person changes (the New Note modal also
  // refreshes the notes store after a save, so the list stays current without navigation).
  $effect(() => {
    const id = $route.personId;
    if (id) loadNotes(id);
  });

  // Keep a valid selection: default to the newest note, fall back if it disappears.
  $effect(() => {
    if (notes.length && !notes.some((n) => n.id === selectedNoteId)) {
      selectedNoteId = notes[0].id;
    }
  });

  const selectedNote = $derived(notes.find((n) => n.id === selectedNoteId) ?? notes[0] ?? null);

  const tabs = $derived([
    ["overview", "Overview", null],
    ["notes", "1:1 Notes", notes.length],
    ["goals", "Goals", null],
    ["feedback", "Feedback", null],
    ["growth", "Growth", null],
    ["review", "Review prep", null],
  ]);
</script>

{#if person}
  <div class="page" style="max-width:1280px;" data-screen-label="Person · {person.name}">
    <!-- Breadcrumb -->
    <div class="row" style="gap:6px; margin-bottom:20px; color:var(--fg-3); font-size:13px;">
      <button class="btn btn-ghost btn-sm" onclick={() => goTo("team")} style="padding:4px 8px;">
        <Icon name="chevleft" size={14} /> Team
      </button>
      <span>/</span>
      <span style="color:var(--fg-1); font-weight:500;">{person.name}</span>
    </div>

    <!-- Header -->
    <div class="row" style="gap:20px; margin-bottom:28px; align-items:flex-start;">
      <Avatar {person} size="xl" />
      <div style="flex:1;">
        <div class="row" style="gap:8px;">
          <h1 class="display" style="font-size:32px; margin:0;">{person.name}</h1>
          {#if person.pronouns}
            <span class="meta" style="align-self:center;">{person.pronouns}</span>
          {/if}
        </div>
        <div style="color:var(--fg-2); font-size:15px; margin-top:4px;">
          {person.role} · <span class="mono">{person.level}</span> · {person.tenure} · {person.timezone}
        </div>
        <div class="row" style="gap:6px; margin-top:10px; flex-wrap:wrap;">
          {#each person.tags ?? [] as t (t)}
            <span class="chip">#{t}</span>
          {/each}
          {#each person.flags ?? [] as f (f)}
            <Flag kind={f} />
          {/each}
          {#if person.pto}
            <span class="chip chip-warn"><Icon name="pto" size={10} /> {person.pto}</span>
          {/if}
        </div>
      </div>
      <div class="row" style="gap:8px;">
        <button class="btn btn-outline btn-sm"><Icon name="calendar" size={14} /> Schedule</button>
        <button class="btn btn-accent btn-sm" onclick={() => openNewNote(person)}>
          <Icon name="plus" size={14} /> New 1:1 note <Kbd>N</Kbd>
        </button>
      </div>
    </div>

    <!-- Quick stats strip -->
    <div
      class="row"
      style="gap:0; margin-bottom:24px; background:var(--bg-surface); border:1px solid var(--line); border-radius:var(--radius-card); overflow:hidden;"
    >
      <div style="flex:1; padding:14px 18px; border-right:1px solid var(--line);">
        <div class="eyebrow" style="font-size:10px; margin-bottom:4px;">Next 1:1</div>
        <div style="font-size:18px; font-weight:500; color:var(--fg-1);">{person.nextOneOnOne}</div>
      </div>
      <div style="flex:1; padding:14px 18px; border-right:1px solid var(--line);">
        <div class="eyebrow" style="font-size:10px; margin-bottom:4px;">Last note</div>
        <div style="font-size:18px; font-weight:500; color:var(--fg-1);">{person.lastNote}</div>
      </div>
      <div style="flex:1; padding:14px 18px; border-right:1px solid var(--line);">
        <div class="eyebrow" style="font-size:10px; margin-bottom:4px;">Sentiment</div>
        <div style="font-size:18px; font-weight:500; color:var(--fg-1);">
          <div class="row" style="gap:8px;">
            {#if lastScore != null}
              <span class="mono">{lastScore}/5</span>
              <Sparkline values={person.sentiment} width={56} height={18} accent={(person.flags ?? []).includes("sentiment-drop")} />
            {:else}
              <span class="meta">—</span>
            {/if}
          </div>
        </div>
        <div class="meta" style="font-size:12px; margin-top:2px;">{person.sentimentLabel}</div>
      </div>
      <div style="flex:1; padding:14px 18px; border-right:1px solid var(--line);">
        <div class="eyebrow" style="font-size:10px; margin-bottom:4px;">Open actions</div>
        <div style="font-size:18px; font-weight:500; color:var(--fg-1);">{person.openActions}</div>
        <div class="meta" style="font-size:12px; margin-top:2px;">
          {person.openActions > 3 ? "above threshold" : "manageable"}
        </div>
      </div>
      <div style="flex:1; padding:14px 18px;">
        <div class="eyebrow" style="font-size:10px; margin-bottom:4px;">Growth</div>
        <div style="font-size:18px; font-weight:500; color:var(--fg-1);">
          {Math.round((person.growthProgress ?? 0) * 100)}%
        </div>
        <div class="meta" style="font-size:12px; margin-top:2px;">{person.growthFocus}</div>
      </div>
    </div>

    <!-- Tabs -->
    <div class="tabs" style="margin-bottom:20px;">
      {#each tabs as [k, label, count] (k)}
        <button class="tab {tab === k ? 'active' : ''}" onclick={() => (tab = k)}>
          {label}{#if count != null}<span class="mono" style="margin-left:6px; color:var(--fg-4); font-size:12px;">{count}</span>{/if}
        </button>
      {/each}
    </div>

    {#if tab === "overview"}
      <div style="display:grid; grid-template-columns:1.5fr 1fr; gap:24px;">
        <div class="stack">
          <AICard title="Recent signals" />

          {#snippet viewNotes()}
            <button class="btn btn-ghost btn-sm" onclick={() => (tab = "notes")}>
              View all <Icon name="chevright" size={12} />
            </button>
          {/snippet}
          <SectionHeader icon="note" title="Most recent 1:1" action={viewNotes} />
          {#if selectedNote}
            <div class="card card-pad">
              <div class="between" style="margin-bottom:8px;">
                <div class="row" style="gap:10px;">
                  <span class="chip">{selectedNote.type}</span>
                  <span class="meta">{selectedNote.date} · {selectedNote.duration}</span>
                </div>
                {#if selectedNote.sentiment != null}
                  <SentimentDot score={selectedNote.sentiment} />
                {/if}
              </div>
              <div style="font-size:15px; color:var(--fg-2); line-height:1.6; margin-bottom:12px;">
                {selectedNote.summary}
              </div>
              {#if selectedNote.highlights?.length}
                <ul style="margin:0; padding-left:18px; color:var(--fg-2); font-size:14px;">
                  {#each selectedNote.highlights as h (h)}
                    <li>{h}</li>
                  {/each}
                </ul>
              {/if}
            </div>
          {:else}
            <div class="card card-pad meta">No notes yet.</div>
          {/if}

          <SectionHeader icon="target" title="Current goals" />
          <div class="card card-pad meta">
            Goals aren't tracked in this local build yet.
          </div>
        </div>

        <div class="stack">
          <SectionHeader icon="actions" title="Open actions" />
          <div class="card">
            {#each personActions as a (a.id)}
              <div class="list-row" style="grid-template-columns:auto 1fr auto;">
                <input
                  type="checkbox"
                  checked={a.done}
                  onclick={() => toggleAction(a.id)}
                  style="width:16px; height:16px; accent-color:var(--accent);"
                />
                <div style="font-size:14px; color:var(--fg-1);">{a.text}</div>
                <span class="chip chip-draft">{a.owner === "me" ? "You" : person.name.split(" ")[0]}</span>
              </div>
            {/each}
            {#if personActions.length === 0}
              <div class="card-pad meta">No open actions.</div>
            {/if}
          </div>

          <SectionHeader icon="trend_up" title="Sentiment · {person.sentiment?.length ?? 0} wks" />
          <div class="card card-pad">
            <div class="row" style="gap:12px; align-items:flex-end; justify-content:space-between;">
              <div>
                <div class="display" style="font-size:32px; line-height:1;">
                  {lastScore ?? "—"}<span style="font-size:14px; color:var(--fg-3);">/5</span>
                </div>
                <div class="meta">{person.sentimentLabel}</div>
              </div>
              <Sparkline values={person.sentiment} width={150} height={50} accent={(person.flags ?? []).includes("sentiment-drop")} />
            </div>
          </div>

          <SectionHeader icon="growth" title="Growth" />
          <div class="card card-pad">
            <div class="meta">Focus</div>
            <div style="font-weight:600; margin-bottom:6px;">{person.growthFocus}</div>
            <div style="margin-top:12px;"><Bar value={person.growthProgress ?? 0} color="var(--accent)" /></div>
            <div class="meta mono" style="margin-top:6px;">{Math.round((person.growthProgress ?? 0) * 100)}% to target</div>
          </div>
        </div>
      </div>
    {/if}

    {#if tab === "notes"}
      <div style="display:grid; grid-template-columns:260px 1fr; gap:24px;">
        <div>
          <button
            class="btn btn-accent"
            style="width:100%; justify-content:center; margin-bottom:12px;"
            onclick={() => openNewNote(person)}
          >
            <Icon name="plus" size={14} /> New 1:1 note
          </button>
          <div class="stack" style="--stack:4px;">
            {#each notes as n (n.id)}
              <button
                onclick={() => (selectedNoteId = n.id)}
                class="card hover"
                style="width:100%; text-align:left; padding:12px; cursor:pointer; background:var(--bg-surface); border-color:{selectedNoteId === n.id ? 'var(--fg-1)' : 'var(--line)'}; border-width:{selectedNoteId === n.id ? 2 : 1}px;"
              >
                <div class="between" style="margin-bottom:4px;">
                  <span class="mono" style="font-size:12px; color:var(--fg-3);">{n.date}</span>
                  {#if n.sentiment != null}<SentimentDot score={n.sentiment} />{/if}
                </div>
                <div style="font-size:13px; color:var(--fg-2); line-height:1.4;">
                  {n.summary.slice(0, 70)}…
                </div>
                <div class="row" style="gap:4px; margin-top:6px;">
                  <span class="chip" style="font-size:10px;">{n.type}</span>
                  {#if n.actions?.length}
                    <span class="chip chip-draft" style="font-size:10px;">
                      {n.actions.length} action{n.actions.length > 1 ? "s" : ""}
                    </span>
                  {/if}
                </div>
              </button>
            {/each}
            {#if notes.length === 0}
              <div class="card card-pad meta">No notes yet.</div>
            {/if}
          </div>
        </div>

        {#if selectedNote}
          <div class="card card-pad">
            <div
              class="between"
              style="margin-bottom:16px; padding-bottom:16px; border-bottom:1px solid var(--line);"
            >
              <div>
                <div class="row" style="gap:10px;">
                  <span class="chip">{selectedNote.type}</span>
                  <span class="display" style="font-size:22px;">{selectedNote.date}</span>
                </div>
                <div class="meta" style="margin-top:4px;">{selectedNote.duration} · with {person.name}</div>
              </div>
            </div>

            <div class="prose">
              {#if selectedNote.highlights?.length}
                <h2>Highlights</h2>
                <ul>
                  {#each selectedNote.highlights as h (h)}
                    <li>{h}</li>
                  {/each}
                </ul>
              {/if}

              <h2>Summary</h2>
              <p>{selectedNote.summary}</p>

              {#if selectedNote.actions?.length}
                <h2>Action items</h2>
                <ul style="list-style:none; padding:0;">
                  {#each selectedNote.actions as a (a.id)}
                    <li style="padding:8px 0; display:flex; gap:10px; align-items:flex-start;">
                      <input
                        type="checkbox"
                        checked={a.done}
                        onclick={() => toggleAction(a.id)}
                        style="margin-top:4px; accent-color:var(--accent);"
                      />
                      <div>
                        <div style="text-decoration:{a.done ? 'line-through' : 'none'}; color:{a.done ? 'var(--fg-4)' : 'var(--fg-1)'};">
                          {a.text}
                        </div>
                        <div class="meta">Owner: {a.owner === "me" ? "You" : person.name.split(" ")[0]}</div>
                      </div>
                    </li>
                  {/each}
                </ul>
              {/if}
            </div>
          </div>
        {/if}
      </div>
    {/if}

    {#if tab === "goals" || tab === "feedback" || tab === "growth" || tab === "review"}
      <div class="card card-pad" style="text-align:center; padding:48px;">
        <div class="display" style="font-size:22px; margin-bottom:6px;">Not in this local build</div>
        <div class="meta">
          {tab === "goals" ? "Goals" : tab === "feedback" ? "Feedback" : tab === "growth" ? "Growth plans" : "Review prep"}
          aren't part of the MVP data model yet. The schema reserves space for them.
        </div>
      </div>
    {/if}
  </div>
{:else}
  <div class="page meta">Loading person…</div>
{/if}
