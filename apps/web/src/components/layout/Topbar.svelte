<script>
  import Wordmark from "../atoms/Wordmark.svelte";
  import Icon from "../atoms/Icon.svelte";
  import Kbd from "../atoms/Kbd.svelte";
  import Avatar from "../atoms/Avatar.svelte";

  // The signed-in manager. Single-user local app, so this is a fixed identity for now.
  const me = { initials: "JC", color: "var(--accent)" };

  let { onOpenPalette = () => {} } = $props();
</script>

<div class="app-topbar">
  <Wordmark size={18} />
  <div style="flex:1"></div>
  <button
    onclick={onOpenPalette}
    class="row"
    style="gap:8px; padding:6px 12px; border-radius:var(--radius-input); background:var(--bg-surface-2); border:1px solid var(--line); color:var(--fg-3); cursor:pointer; font-size:13px; min-width:320px;"
  >
    <Icon name="search" size={14} />
    <span style="flex:1; text-align:left;">Search team, notes, actions…</span>
    <Kbd>⌘K</Kbd>
  </button>
  <button class="btn-icon" title="Notifications"><Icon name="bell" size={18} /></button>
  <Avatar person={me} size="md" />
</div>
