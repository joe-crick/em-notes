<script>
  import Sparkline from "../atoms/Sparkline.svelte";
  import Icon from "../atoms/Icon.svelte";

  // Aggregate sentiment across the team (ported from screens-home-team.jsx). All values are
  // real, derived from each person's sentiment trend.
  let { people = [] } = $props();

  const withTrends = $derived(people.filter((p) => p.sentiment?.length));
  const weeks = $derived(withTrends.length ? Math.min(...withTrends.map((p) => p.sentiment.length)) : 0);

  const latest = $derived(withTrends.map((p) => p.sentiment[p.sentiment.length - 1]));
  const avg = $derived(
    latest.length ? (latest.reduce((a, b) => a + b, 0) / latest.length).toFixed(1) : "—"
  );

  // Per-week team average over the shared window.
  const weekly = $derived(
    Array.from({ length: weeks }, (_, w) => {
      const sum = withTrends.reduce((acc, p) => acc + p.sentiment[w], 0);
      return Math.round((sum / withTrends.length) * 10) / 10;
    })
  );

  const delta = $derived(
    weekly.length >= 2 ? Math.round((weekly[weekly.length - 1] - weekly[weekly.length - 2]) * 10) / 10 : 0
  );
</script>

<div class="card card-pad">
  <div class="between" style="align-items:flex-end; margin-bottom:12px;">
    <div>
      <div class="display" style="font-size:32px; line-height:1;">
        {avg}<span style="font-size:16px; color:var(--fg-3);">/5</span>
      </div>
      <div class="meta">Avg team sentiment this week</div>
    </div>
    {#if delta !== 0}
      <span class="chip {delta < 0 ? 'chip-warn' : 'chip-ok'}">
        <Icon name={delta < 0 ? "trend_down" : "trend_up"} size={12} />
        {delta > 0 ? "+" : ""}{delta} vs last wk
      </span>
    {/if}
  </div>
  <Sparkline values={weekly} width={280} height={48} accent />
  <div
    class="row"
    style="justify-content:space-between; margin-top:6px; font-size:11px; color:var(--fg-4); font-family:var(--font-mono);"
  >
    <span>{weeks} wks ago</span><span>now</span>
  </div>
</div>
