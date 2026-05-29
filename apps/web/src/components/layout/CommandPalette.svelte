<script>
  import { people } from "../../lib/stores/people.js";
  import { goTo } from "../../lib/stores/route.js";
  import { logout } from "../../lib/stores/session.js";
  import { closePalette, openNewNote } from "../../lib/stores/ui.js";
  import Icon from "../atoms/Icon.svelte";
  import Avatar from "../atoms/Avatar.svelte";
  import Kbd from "../atoms/Kbd.svelte";

  // Command palette (ported from app.jsx). Navigate routes, jump to people, start a note, or
  // sign out — all keyboard-driven. Cmd/Ctrl+K and `/` open it (see lib/keyboard/shortcuts.js).
  let query = $state("");
  let idx = $state(0);
  let inputEl = $state(null);

  const run = (fn) => {
    closePalette();
    fn();
  };

  const allItems = $derived([
    { kind: "nav", label: "Go to Home", icon: "home", run: () => goTo("home") },
    { kind: "nav", label: "Go to Team", icon: "team", run: () => goTo("team") },
    { kind: "nav", label: "Go to Actions", icon: "actions", run: () => goTo("actions") },
    { kind: "nav", label: "Go to Settings", icon: "settings", run: () => goTo("settings") },
    ...$people.map((p) => ({
      kind: "person",
      label: p.name,
      sub: p.role,
      person: p,
      run: () => goTo("person", p.id),
    })),
    ...$people.map((p) => ({
      kind: "action",
      label: `New 1:1 note → ${p.name}`,
      icon: "plus",
      person: p,
      run: () => openNewNote(p),
    })),
    { kind: "action", label: "Sign out", icon: "arrow", run: () => logout() },
  ]);

  const items = $derived(
    query
      ? allItems.filter((it) => {
          const q = query.toLowerCase();
          return it.label.toLowerCase().includes(q) || (it.sub || "").toLowerCase().includes(q);
        })
      : allItems
  );

  // Keep the highlighted index in range as the list shrinks.
  $effect(() => {
    if (idx > items.length - 1) idx = Math.max(0, items.length - 1);
  });

  $effect(() => {
    inputEl?.focus();
  });

  function onKey(e) {
    if (e.key === "ArrowDown") {
      e.preventDefault();
      idx = Math.min(items.length - 1, idx + 1);
    } else if (e.key === "ArrowUp") {
      e.preventDefault();
      idx = Math.max(0, idx - 1);
    } else if (e.key === "Enter") {
      e.preventDefault();
      const it = items[idx];
      if (it) run(it.run);
    }
    // Escape is handled by the global shortcut handler.
  }
</script>

<div
  class="modal-scrim"
  onclick={(e) => e.target === e.currentTarget && closePalette()}
  role="presentation"
>
  <div class="modal" style="width:600px;" role="dialog" aria-modal="true" tabindex="-1">
    <div class="row" style="gap:10px; padding:14px 18px; border-bottom:1px solid var(--line);">
      <Icon name="search" size={18} color="var(--fg-3)" />
      <!-- svelte-ignore a11y_autofocus -->
      <input
        bind:this={inputEl}
        bind:value={query}
        onkeydown={onKey}
        placeholder="Search team, jump to page, run command…"
        style="border:none; outline:none; flex:1; font:400 16px/1.4 var(--font-ui); background:transparent; color:var(--fg-1);"
      />
      <Kbd>esc</Kbd>
    </div>

    <div style="max-height:400px; overflow-y:auto; padding:8px;">
      {#if items.length === 0}
        <div class="meta" style="padding:20px; text-align:center;">No matches</div>
      {/if}
      {#each items as it, i (it.label)}
        <button
          onclick={() => run(it.run)}
          onmouseenter={() => (idx = i)}
          class="row"
          style="width:100%; padding:10px 14px; background:{i === idx
            ? 'var(--bg-surface-2)'
            : 'transparent'}; border:none; border-radius:var(--radius-input); cursor:pointer; text-align:left; gap:12px; color:var(--fg-1);"
        >
          {#if it.person}
            <Avatar person={it.person} size="sm" />
          {:else}
            <Icon name={it.icon} size={16} color="var(--fg-3)" />
          {/if}
          <div style="flex:1;">
            <div style="font-size:14px; font-weight:500;">{it.label}</div>
            {#if it.sub}<div class="meta">{it.sub}</div>{/if}
          </div>
          <span class="chip chip-draft" style="font-size:10px;">{it.kind}</span>
        </button>
      {/each}
    </div>

    <div
      class="row"
      style="gap:12px; padding:10px 18px; border-top:1px solid var(--line); color:var(--fg-4); font-size:11px;"
    >
      <span><Kbd>↑</Kbd> <Kbd>↓</Kbd> navigate</span>
      <span><Kbd>↵</Kbd> select</span>
      <span><Kbd>esc</Kbd> close</span>
    </div>
  </div>
</div>
