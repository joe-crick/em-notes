<script>
  import { people } from "../lib/stores/people.js";
  import { goTo } from "../lib/stores/route.js";
  import { openAddReport } from "../lib/stores/ui.js";
  import Icon from "../components/atoms/Icon.svelte";
  import Avatar from "../components/atoms/Avatar.svelte";
  import Sparkline from "../components/atoms/Sparkline.svelte";
  import Bar from "../components/atoms/Bar.svelte";
  import Flag from "../components/atoms/Flag.svelte";
  import Kbd from "../components/atoms/Kbd.svelte";
  import { ME } from "../lib/manager.js";

  let query = $state("");
  let filter = $state("all");

  const filters = [
    ["all", "All"],
    ["flagged", "Flagged"],
    ["promo", "Promo-ready"],
    ["new", "New hires"],
  ];

  const cols = "2fr 1.2fr 1fr 1.2fr 0.8fr 1fr 0.6fr";
  const headers = ["Name", "Role · Level", "Next 1:1", "Growth focus", "Sentiment", "Flags", "Open"];

  const filtered = $derived(
    $people.filter((p) => {
      const flags = p.flags ?? [];
      if (filter === "flagged" && !flags.length) return false;
      if (filter === "promo" && !flags.includes("promotion-ready")) return false;
      if (filter === "new" && !flags.includes("new-hire")) return false;
      if (query) {
        const hay = `${p.name} ${p.role} ${(p.tags ?? []).join(" ")}`.toLowerCase();
        if (!hay.includes(query.toLowerCase())) return false;
      }
      return true;
    })
  );
</script>

<div class="page" data-screen-label="Team">
  <div class="between" style="margin-bottom:24px;">
    <div>
      <div class="eyebrow">Team</div>
      <h1 class="display" style="font-size:36px; margin:4px 0;">
        {ME.team} · {$people.length} people
      </h1>
    </div>
    <div class="row" style="gap:8px;">
      <button class="btn btn-primary btn-sm" onclick={openAddReport}>
        <Icon name="plus" size={14} /> Add report
      </button>
    </div>
  </div>

  <div class="row" style="gap:12px; margin-bottom:18px; flex-wrap:wrap;">
    <div
      class="row"
      style="flex:1; gap:8px; padding:8px 12px; background:var(--bg-surface); border:1px solid var(--line-strong); border-radius:var(--radius-input); max-width:360px;"
    >
      <Icon name="search" size={16} color="var(--fg-3)" />
      <input
        bind:value={query}
        placeholder="Search team, tag, role…"
        style="border:none; outline:none; background:transparent; flex:1; font:400 14px/1 var(--font-ui); color:var(--fg-1);"
      />
      <Kbd>/</Kbd>
    </div>
    <div class="row" style="gap:4px;">
      {#each filters as [k, label] (k)}
        <button
          class="btn btn-sm {filter === k ? 'btn-primary' : 'btn-ghost'}"
          onclick={() => (filter = k)}
        >
          {label}
        </button>
      {/each}
    </div>
  </div>

  <div class="card" style="padding:0; overflow:hidden;">
    <div
      class="list-row"
      style="grid-template-columns:{cols}; cursor:default; background:var(--bg-surface-2); border-bottom:1px solid var(--line-strong); padding:10px 16px;"
    >
      {#each headers as h (h)}
        <div class="eyebrow" style="font-size:10px;">{h}</div>
      {/each}
    </div>
    {#each filtered as p (p.id)}
      <div
        class="list-row"
        style="grid-template-columns:{cols};"
        onclick={() => goTo("person", p.id)}
        onkeydown={(e) => e.key === "Enter" && goTo("person", p.id)}
        role="button"
        tabindex="0"
      >
        <div class="row" style="gap:12px;">
          <Avatar person={p} size="md" />
          <div>
            <div style="font-weight:600; color:var(--fg-1);">{p.name}</div>
            <div class="meta">{p.timezone}</div>
          </div>
        </div>
        <div>
          <div style="font-size:14px;">{p.role}</div>
          <div class="meta mono">{p.level} · {p.tenure}</div>
        </div>
        <div class="mono" style="font-size:13px;">{p.nextOneOnOne}</div>
        <div style="font-size:13px; color:var(--fg-2);">
          {p.growthFocus}
          <div style="margin-top:4px; width:100px;"><Bar value={p.growthProgress} /></div>
        </div>
        <div>
          <Sparkline values={p.sentiment} width={64} height={20} accent={(p.flags ?? []).includes("sentiment-drop")} />
        </div>
        <div class="row" style="gap:4px; flex-wrap:wrap;">
          {#each p.flags ?? [] as f (f)}
            <Flag kind={f} />
          {/each}
          {#if p.pto}
            <span class="chip"><Icon name="pto" size={10} /> PTO</span>
          {/if}
        </div>
        <div
          class="mono"
          style="font-size:13px; color:{p.openActions > 3 ? 'var(--status-warn)' : 'var(--fg-3)'};"
        >
          {p.openActions}
        </div>
      </div>
    {/each}
    {#if filtered.length === 0}
      <div class="card-pad meta">No one matches that filter.</div>
    {/if}
  </div>
</div>
