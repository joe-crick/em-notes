<script>
  // Three notation strokes (ledger/note lines) with an accent "pen tip" dot — the EM Notes mark.
  let { size = 28 } = $props();
</script>

<svg width={size} height={size} viewBox="0 0 40 40" fill="none" aria-label="EM Notes">
  <rect x="6" y="9" width="22" height="4" rx="1" fill="currentColor" />
  <rect x="6" y="18" width="28" height="4" rx="1" fill="currentColor" />
  <rect x="6" y="27" width="16" height="4" rx="1" fill="currentColor" />
  <circle cx="32" cy="29" r="3" fill="var(--accent)" />
</svg>
