<script>
  import Icon from "../atoms/Icon.svelte";
  import Avatar from "../atoms/Avatar.svelte";

  let { icon, avatar, label, sub, active = false, onClick, badge, shortcut } = $props();
</script>

<button
  class="nav-item {active ? 'active' : ''}"
  onclick={onClick}
  title={shortcut ? `Shortcut: ${shortcut}` : ""}
>
  {#if avatar}
    <Avatar person={avatar} size="sm" />
  {:else}
    <Icon name={icon} size={16} />
  {/if}
  <span
    style="flex:1; text-align:left; overflow:hidden; text-overflow:ellipsis; white-space:nowrap;"
    >{label}</span
  >
  {#if sub}
    <span style="font-size:11px; color:{active ? 'rgba(255,255,255,0.7)' : 'var(--fg-3)'};"
      >{sub}</span
    >
  {/if}
  {#if badge != null}
    <span
      class="chip"
      style="background:{active
        ? 'rgba(255,255,255,0.15)'
        : 'var(--bg-surface-2)'}; color:{active
        ? 'var(--fg-on-dark)'
        : 'var(--fg-3)'}; font-size:11px; padding:1px 7px;">{badge}</span
    >
  {/if}
</button>
