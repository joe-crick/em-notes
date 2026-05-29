<script>
  // Progress bar (ported from atoms.jsx).
  let { value = 0, max = 1, color = "var(--accent)", height = 6 } = $props();

  const w = $derived(Math.max(0, Math.min(1, value / max)) * 100);
</script>

<div
  style="background:var(--bg-surface-2); height:{height}px; border-radius:999px; overflow:hidden; width:100%;"
>
  <div
    style="width:{w}%; height:100%; background:{color}; border-radius:999px; transition:width 240ms var(--ease);"
  ></div>
</div>
