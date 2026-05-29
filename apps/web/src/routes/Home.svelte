<script>
  import { people } from "../lib/stores/people.js";
  import { actions, toggleAction } from "../lib/stores/actions.js";
  import { agenda as calendarAgenda } from "../lib/stores/calendar.js";
  import { goTo } from "../lib/stores/route.js";
  import { openNewNote } from "../lib/stores/ui.js";
  import { ME, greeting } from "../lib/manager.js";
  import Icon from "../components/atoms/Icon.svelte";
  import Avatar from "../components/atoms/Avatar.svelte";
  import Sparkline from "../components/atoms/Sparkline.svelte";
  import Flag from "../components/atoms/Flag.svelte";
  import SectionHeader from "../components/SectionHeader.svelte";
  import TeamPulseCard from "../components/team/TeamPulseCard.svelte";

  const byId = $derived(Object.fromEntries($people.map((p) => [p.id, p])));
  const openActions = $derived($actions.filter((a) => !a.done));
  const flagged = $derived($people.filter((p) => p.flags?.length));
  const onPto = $derived($people.filter((p) => p.pto));

  // Prefer real upcoming events from synced calendar feeds; fall back to each report's scheduled
  // next 1:1 when no feed is connected.
  const hasCalendar = $derived($calendarAgenda.length > 0);
  const derivedAgenda = $derived($people.filter((p) => p.nextOneOnOne));

  function fmtWhen(iso) {
    return new Intl.DateTimeFormat("en-US", {
      weekday: "short",
      hour: "numeric",
      minute: "2-digit",
      timeZone: "UTC",
    }).format(new Date(iso));
  }

  const today = new Intl.DateTimeFormat("en-US", {
    weekday: "long",
    month: "long",
    day: "numeric",
    year: "numeric",
  }).format(new Date());
</script>

<div class="page" data-screen-label="Home">
  <header style="margin-bottom:28px;">
    <div class="eyebrow">{today}</div>
    <h1 class="display" style="font-size:42px; margin:8px 0 4px;">
      {greeting()}, {ME.name.split(" ")[0]}.
    </h1>
    <p style="color:var(--fg-3); font-size:16px; margin:0;">
      You manage <strong style="color:var(--fg-1);">{$people.length} direct reports</strong> and have
      <strong style="color:var(--fg-1);">{openActions.length} open actions</strong>.
    </p>
  </header>

  <div style="display:grid; grid-template-columns:1.4fr 1fr; gap:24px;">
    <!-- LEFT: upcoming 1:1s + open actions -->
    <div class="stack">
      <SectionHeader icon="calendar" title={hasCalendar ? "Upcoming" : "Upcoming 1:1s"} />
      <div class="card">
        {#if hasCalendar}
          {#each $calendarAgenda as e (e.id)}
            {@const person = e.personId ? byId[e.personId] : null}
            <div
              class="list-row"
              style="grid-template-columns:110px 1fr auto;"
              onclick={() => person && goTo("person", person.id)}
              onkeydown={(ev) => ev.key === "Enter" && person && goTo("person", person.id)}
              role="button"
              tabindex="0"
            >
              <div class="mono" style="font-size:13px; color:var(--fg-3);">{fmtWhen(e.startsAt)}</div>
              <div class="row" style="gap:12px;">
                {#if person}
                  <Avatar {person} size="md" />
                {:else}
                  <span class="avatar avatar-md" style="background:var(--bg-surface-2); color:var(--fg-3);">
                    <Icon name="calendar" size={14} />
                  </span>
                {/if}
                <div>
                  <div style="font-weight:500; color:var(--fg-1);">{e.summary || "Untitled event"}</div>
                  <div class="meta">{person ? person.name : e.location || "Calendar"}</div>
                </div>
              </div>
              {#if person}<Icon name="chevright" size={16} color="var(--fg-4)" />{/if}
            </div>
          {/each}
        {:else}
          {#each derivedAgenda as p (p.id)}
            <div
              class="list-row"
              style="grid-template-columns:90px 1fr auto;"
              onclick={() => goTo("person", p.id)}
              onkeydown={(e) => e.key === "Enter" && goTo("person", p.id)}
              role="button"
              tabindex="0"
            >
              <div class="mono" style="font-size:13px; color:var(--fg-3);">{p.nextOneOnOne}</div>
              <div class="row" style="gap:12px;">
                <Avatar person={p} size="md" />
                <div>
                  <div style="font-weight:500; color:var(--fg-1);">1:1 — {p.name}</div>
                  <div class="meta">{p.role}</div>
                </div>
              </div>
              <Icon name="chevright" size={16} color="var(--fg-4)" />
            </div>
          {/each}
          {#if derivedAgenda.length === 0}
            <div class="card-pad meta">No 1:1s scheduled. Connect a calendar in Settings.</div>
          {/if}
        {/if}
      </div>

      {#snippet viewAll()}
        <button class="btn btn-ghost btn-sm" onclick={() => goTo("actions")}>
          View all <Icon name="chevright" size={12} />
        </button>
      {/snippet}
      <SectionHeader icon="actions" title="Open action items" badge={openActions.length} action={viewAll} />
      <div class="card">
        {#each openActions.slice(0, 6) as a (a.id)}
          {@const person = a.personId ? byId[a.personId] : null}
          <div
            class="list-row"
            style="grid-template-columns:auto 1fr auto auto; gap:12px;"
            onclick={() => person && goTo("person", person.id)}
            onkeydown={(e) => e.key === "Enter" && person && goTo("person", person.id)}
            role="button"
            tabindex="0"
          >
            <input
              type="checkbox"
              checked={a.done}
              onclick={(e) => {
                e.stopPropagation();
                toggleAction(a.id);
              }}
              style="width:16px; height:16px; accent-color:var(--accent);"
            />
            <div>
              <div style="font-weight:500; color:var(--fg-1);">{a.text}</div>
              <div class="meta">{person ? person.name : "Team-wide"}</div>
            </div>
            <span class="chip chip-draft">{a.owner === "me" ? "You" : "Report"}</span>
            {#if person}
              <Avatar {person} size="sm" />
            {:else}
              <span class="chip chip-draft">Team</span>
            {/if}
          </div>
        {/each}
        {#if openActions.length === 0}
          <div class="card-pad meta">No open actions. All clear.</div>
        {/if}
      </div>
    </div>

    <!-- RIGHT: signal column -->
    <div class="stack">
      <SectionHeader icon="bell" title="Needs your attention" />
      <div class="card card-pad">
        {#if flagged.length === 0}
          <div class="meta">Nothing flagged. Quiet week.</div>
        {:else}
          <div class="stack" style="--stack:14px;">
            {#each flagged as p (p.id)}
              <div
                class="row"
                style="gap:12px; cursor:pointer;"
                onclick={() => goTo("person", p.id)}
                onkeydown={(e) => e.key === "Enter" && goTo("person", p.id)}
                role="button"
                tabindex="0"
              >
                <Avatar person={p} size="md" />
                <div style="flex:1;">
                  <div class="row" style="gap:6px;">
                    <span style="font-weight:600; color:var(--fg-1);">{p.name}</span>
                    {#each p.flags as f (f)}
                      <Flag kind={f} />
                    {/each}
                  </div>
                  <div class="meta">{p.sentimentLabel} · last note {p.lastNote}</div>
                </div>
                <Sparkline
                  values={p.sentiment}
                  width={64}
                  height={20}
                  accent={p.flags.includes("sentiment-drop")}
                />
              </div>
            {/each}
          </div>
        {/if}
      </div>

      <SectionHeader icon="pto" title="This week's PTO" />
      <div class="card card-pad">
        {#if onPto.length === 0}
          <div class="meta">No team PTO this week.</div>
        {:else}
          <div class="stack" style="--stack:12px;">
            {#each onPto as p (p.id)}
              <div class="row" style="gap:12px;">
                <Avatar person={p} size="md" />
                <div style="flex:1;">
                  <div style="font-weight:500;">{p.name}</div>
                  <div class="meta">{p.pto}</div>
                </div>
              </div>
            {/each}
          </div>
        {/if}
      </div>

      <SectionHeader icon="trend_up" title="Team pulse" />
      <TeamPulseCard people={$people} />
    </div>
  </div>
</div>
