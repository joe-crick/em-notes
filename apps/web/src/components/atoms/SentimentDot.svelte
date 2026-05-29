<script>
  // Sentiment score badge (ported from atoms.jsx): green ≥4, amber ≥3, red below.
  let { score } = $props();

  const color = $derived(
    score >= 4 ? "var(--status-ok)" : score >= 3 ? "var(--status-warn)" : "var(--status-err)"
  );
</script>

<span
  style="display:inline-flex; align-items:center; justify-content:center; width:20px; height:20px; border-radius:999px; background:color-mix(in srgb, {color} 18%, transparent); color:{color}; font-size:11px; font-weight:700; font-family:var(--font-mono);"
>
  {score}
</span>
