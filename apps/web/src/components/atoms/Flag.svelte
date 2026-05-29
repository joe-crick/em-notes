<script>
  // Flag pill (ported from atoms.jsx). Maps a flag kind to a labelled chip.
  let { kind } = $props();

  const config = {
    "sentiment-drop": { label: "Sentiment ↓", cls: "chip-err" },
    "promotion-ready": { label: "Promo-ready", cls: "chip-accent" },
    "new-hire": { label: "New hire", cls: "chip-ok" },
    "no-note-7d": { label: "7d gap", cls: "chip-warn" },
  };

  const cfg = $derived(config[kind]);
</script>

{#if cfg}
  <span class="chip {cfg.cls}">{cfg.label}</span>
{/if}
