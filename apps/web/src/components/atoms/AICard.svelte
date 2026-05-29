<script>
  import Icon from "./Icon.svelte";

  // AI card wrapper (ported from atoms.jsx). Per plan §1.1/§13.3, no mock AI feature is
  // presented as a real one: with no `children`, it renders a disabled placeholder making
  // clear AI is not implemented in this local build.
  let { title = "AI suggestions", dense = false, children } = $props();
</script>

<div class="card ai-card {dense ? '' : 'card-pad'}" style={dense ? "padding:12px;" : ""}>
  <div class="row" style="margin-bottom:10px; gap:8px;">
    <span style="color:var(--accent); display:inline-flex;">
      <Icon name="sparkles" size={16} />
    </span>
    <span class="eyebrow" style="color:var(--accent);">{title}</span>
  </div>
  {#if children}
    {@render children()}
  {:else}
    <div class="meta" style="font-size:13px; line-height:1.55;">
      AI summaries and analysis aren't part of this local build. The data above is real; nothing
      here is generated.
    </div>
  {/if}
</div>
