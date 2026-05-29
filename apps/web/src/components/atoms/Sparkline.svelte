<script>
  // Sentiment trend sparkline (ported from atoms.jsx). Maps 1–5 scores onto the height.
  let { values = [], width = 80, height = 22, accent = false } = $props();

  const max = 5;
  const min = 1;

  const points = $derived(
    values.length
      ? values.map((v, i) => [
          (i * width) / (values.length - 1),
          height - ((v - min) / (max - min)) * (height - 4) - 2,
        ])
      : []
  );

  const d = $derived(
    points.map((p, i) => `${i === 0 ? "M" : "L"}${p[0].toFixed(1)},${p[1].toFixed(1)}`).join(" ")
  );

  const lastY = $derived(points.length ? points[points.length - 1][1] : 0);
  const color = $derived(accent ? "var(--accent)" : "var(--fg-2)");
</script>

{#if values.length}
  <svg
    {width}
    {height}
    viewBox="0 0 {width} {height}"
    class="sparkline"
    style="display:block;"
  >
    <path {d} stroke={color} stroke-width="1.5" fill="none" stroke-linecap="round" stroke-linejoin="round" />
    <circle cx={width} cy={lastY} r="2.5" fill={color} />
  </svg>
{/if}
