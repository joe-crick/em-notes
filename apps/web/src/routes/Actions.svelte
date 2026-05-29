<script>
  import { actions, toggleAction, addAction } from "../lib/stores/actions.js";
  import { people } from "../lib/stores/people.js";
  import { goTo } from "../lib/stores/route.js";
  import Icon from "../components/atoms/Icon.svelte";
  import Avatar from "../components/atoms/Avatar.svelte";
  import Kbd from "../components/atoms/Kbd.svelte";

  let filter = $state("open"); // open | mine | theirs | done | all
  let personFilter = $state("all");
  let grouping = $state("due"); // due | person | source
  let query = $state("");

  const byId = $derived(Object.fromEntries($people.map((p) => [p.id, p])));

  const todayStr = new Date().toISOString().slice(0, 10);
  function weekAhead() {
    const d = new Date();
    d.setDate(d.getDate() + 7);
    return d.toISOString().slice(0, 10);
  }
  const weekStr = weekAhead();

  const isOverdue = (a) => a.dueAt && a.dueAt < todayStr && !a.done;
  const isToday = (a) => a.dueAt === todayStr;

  const counts = $derived({
    open: $actions.filter((a) => !a.done).length,
    overdue: $actions.filter((a) => isOverdue(a)).length,
    today: $actions.filter((a) => isToday(a) && !a.done).length,
    done: $actions.filter((a) => a.done).length,
    mine: $actions.filter((a) => a.owner === "me" && !a.done).length,
    theirs: $actions.filter((a) => a.owner !== "me" && !a.done).length,
  });

  const filtered = $derived(
    $actions.filter((a) => {
      if (filter === "open" && a.done) return false;
      if (filter === "done" && !a.done) return false;
      if (filter === "mine" && a.owner !== "me") return false;
      if (filter === "theirs" && a.owner === "me") return false;
      if (personFilter !== "all" && a.personId !== personFilter) return false;
      if (query) {
        const person = a.personId ? byId[a.personId]?.name ?? "" : "";
        const hay = `${a.text} ${person}`.toLowerCase();
        if (!hay.includes(query.toLowerCase())) return false;
      }
      return true;
    })
  );

  function sourceLabel(a) {
    return a.noteId ? "1:1 note" : "Quick add";
  }

  function dueBucket(a) {
    if (a.done) return "Done";
    if (isOverdue(a)) return "Overdue";
    if (isToday(a)) return "Today";
    if (a.dueAt && a.dueAt <= weekStr) return "This week";
    if (a.dueAt) return "Later";
    return "No due date";
  }

  const groups = $derived.by(() => {
    const g = {};
    for (const a of filtered) {
      let key;
      if (grouping === "person") key = a.personId ? byId[a.personId]?.name ?? "Unknown" : "Team-wide";
      else if (grouping === "source") key = sourceLabel(a);
      else key = dueBucket(a);
      (g[key] ||= []).push(a);
    }
    if (grouping === "due") {
      const order = ["Overdue", "Today", "This week", "Later", "No due date", "Done"];
      return order.filter((k) => g[k]).map((k) => [k, g[k]]);
    }
    return Object.entries(g);
  });

  const filterTabs = [
    ["open", "Open"],
    ["mine", "Yours"],
    ["theirs", "Theirs"],
    ["done", "Done"],
    ["all", "All"],
  ];

  const personChips = $derived(
    $people
      .map((p) => [p, $actions.filter((a) => a.personId === p.id && !a.done).length])
      .filter(([, n]) => n > 0)
  );

  async function quickAdd() {
    const text = window.prompt("Quick action:");
    if (text && text.trim()) await addAction({ text: text.trim(), owner: "me" });
  }

  function dueChipLabel(a) {
    if (a.done) return "Done";
    if (isOverdue(a)) return "Overdue";
    if (isToday(a)) return "Today";
    return a.dueAt ?? "No date";
  }
</script>

<div class="page" data-screen-label="Actions">
  <div class="between" style="margin-bottom:24px;">
    <div>
      <div class="eyebrow">Actions</div>
      <h1 class="display" style="font-size:36px; margin:4px 0;">
        {counts.open} open · <span style="color:var(--status-err);">{counts.overdue} overdue</span>
      </h1>
    </div>
    <div class="row" style="gap:8px;">
      <button class="btn btn-primary btn-sm" onclick={quickAdd}>
        <Icon name="plus" size={14} /> Quick add
      </button>
    </div>
  </div>

  <!-- Stat strip -->
  <div
    class="row"
    style="gap:0; margin-bottom:18px; background:var(--bg-surface); border:1px solid var(--line); border-radius:var(--radius-card); overflow:hidden;"
  >
    {#snippet stat(label, value, tone, key, last)}
      <button
        onclick={() => key && (filter = key)}
        style="flex:1; padding:14px 18px; border:none; border-right:{last ? 'none' : '1px solid var(--line)'}; background:{key && filter === key ? 'var(--bg-surface-2)' : 'transparent'}; cursor:{key ? 'pointer' : 'default'}; text-align:left;"
      >
        <div class="eyebrow" style="font-size:10px; margin-bottom:4px;">{label}</div>
        <div
          class="display"
          style="font-size:24px; color:{tone === 'err' ? 'var(--status-err)' : tone === 'warn' ? 'var(--status-warn)' : 'var(--fg-1)'};"
        >
          {value}
        </div>
      </button>
    {/snippet}
    {@render stat("Overdue", counts.overdue, "err", "open", false)}
    {@render stat("Today", counts.today, "warn", null, false)}
    {@render stat("Yours", counts.mine, null, "mine", false)}
    {@render stat("Theirs", counts.theirs, null, "theirs", false)}
    {@render stat("Completed", counts.done, null, "done", true)}
  </div>

  <!-- Toolbar -->
  <div class="row" style="gap:12px; margin-bottom:16px; flex-wrap:wrap;">
    <div
      class="row"
      style="flex:1; gap:8px; padding:8px 12px; background:var(--bg-surface); border:1px solid var(--line-strong); border-radius:var(--radius-input); max-width:320px;"
    >
      <Icon name="search" size={16} color="var(--fg-3)" />
      <input
        bind:value={query}
        placeholder="Filter actions…"
        style="border:none; outline:none; background:transparent; flex:1; font:400 14px/1 var(--font-ui); color:var(--fg-1);"
      />
    </div>

    <div class="row" style="gap:4px;">
      {#each filterTabs as [k, label] (k)}
        <button
          class="btn btn-sm {filter === k ? 'btn-primary' : 'btn-ghost'}"
          onclick={() => (filter = k)}
        >
          {label}{#if counts[k] != null}<span class="mono" style="margin-left:4px; font-size:11px; opacity:0.7;">{counts[k]}</span>{/if}
        </button>
      {/each}
    </div>

    <div class="row" style="gap:6px; margin-left:auto;">
      <span class="meta" style="font-size:12px;">Group by</span>
      <select bind:value={grouping} class="input" style="width:auto; padding:6px 8px; font-size:13px;">
        <option value="due">Due date</option>
        <option value="person">Person</option>
        <option value="source">Source</option>
      </select>
    </div>
  </div>

  <!-- Person chips -->
  <div class="row" style="gap:6px; margin-bottom:16px; flex-wrap:wrap;">
    <button
      class="chip {personFilter === 'all' ? 'chip-accent' : ''}"
      style="cursor:pointer;"
      onclick={() => (personFilter = "all")}
    >
      All people
    </button>
    {#each personChips as [p, n] (p.id)}
      <button
        class="chip {personFilter === p.id ? 'chip-accent' : ''}"
        style="cursor:pointer; padding-left:4px;"
        onclick={() => (personFilter = p.id)}
      >
        <Avatar person={p} size="sm" />
        {p.name.split(" ")[0]}
        <span class="mono" style="opacity:0.7;">{n}</span>
      </button>
    {/each}
  </div>

  <!-- Grouped list -->
  <div class="stack">
    {#if groups.length === 0}
      <div class="card card-pad" style="text-align:center; padding:40px;">
        <div class="display" style="font-size:22px; margin-bottom:6px;">Nothing here.</div>
        <div class="meta">No actions match the current filter.</div>
      </div>
    {/if}
    {#each groups as [groupName, items] (groupName)}
      <section>
        <div class="row" style="gap:8px; margin-bottom:8px; margin-top:4px;">
          <div class="eyebrow">{groupName}</div>
          <span class="chip chip-draft" style="font-size:11px;">{items.length}</span>
        </div>
        <div class="card" style="padding:0; overflow:hidden;">
          {#each items as a (a.id)}
            {@const person = a.personId ? byId[a.personId] : null}
            <div
              class="list-row"
              style="grid-template-columns:auto 1fr auto auto auto; gap:14px;"
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
                style="width:18px; height:18px; accent-color:var(--accent);"
              />
              <div>
                <div style="font-size:14px; font-weight:500; color:{a.done ? 'var(--fg-4)' : 'var(--fg-1)'}; text-decoration:{a.done ? 'line-through' : 'none'};">
                  {a.text}
                </div>
                <div class="meta" style="margin-top:2px; display:flex; gap:8px; align-items:center;">
                  <Icon name="link" size={11} />
                  <span>from {sourceLabel(a)}</span>
                </div>
              </div>
              <div>
                {#if person}
                  <div class="row" style="gap:6px;">
                    <Avatar {person} size="sm" />
                    <span style="font-size:13px; color:var(--fg-2);">{person.name.split(" ")[0]}</span>
                  </div>
                {:else}
                  <span class="chip chip-draft">Team-wide</span>
                {/if}
              </div>
              <span class="chip" style="font-size:11px;">{a.owner === "me" ? "You" : "Report"}</span>
              <span
                class="chip {isOverdue(a) ? 'chip-err' : a.done ? 'chip-draft' : isToday(a) ? 'chip-warn' : 'chip-draft'}"
              >
                {dueChipLabel(a)}
              </span>
            </div>
          {/each}
        </div>
      </section>
    {/each}
  </div>

  <div class="meta" style="margin-top:24px; font-size:12px; text-align:center;">
    Tip — actions captured in any 1:1 note appear here automatically. <Kbd>X</Kbd> to toggle complete.
  </div>
</div>
