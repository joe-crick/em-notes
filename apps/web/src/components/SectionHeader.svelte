<script>
  import Icon from "./atoms/Icon.svelte";

  // Section header with icon + title + optional badge/action (ported from screens-home-team.jsx).
  let { icon, title, badge = null, action } = $props();
</script>

<div class="between" style="margin-top:8px;">
  <div class="row" style="gap:8px;">
    <Icon name={icon} size={16} color="var(--fg-3)" />
    <h3
      style="margin:0; font-size:14px; font-weight:600; color:var(--fg-1); letter-spacing:-0.005em;"
    >
      {title}
    </h3>
    {#if badge != null}
      <span class="chip chip-draft" style="font-size:11px;">{badge}</span>
    {/if}
  </div>
  {#if action}
    {@render action()}
  {/if}
</div>
