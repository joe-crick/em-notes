<script>
  import Logo from "./Logo.svelte";
  let { size = 22 } = $props();
</script>

<span style="display:inline-flex; align-items:baseline; gap:8px; color:var(--fg-1);">
  <Logo size={size + 6} />
  <span
    class="display"
    style="font-size:{size}px; font-weight:var(--display-weight); letter-spacing:-0.02em;"
  >
    EM<span style="color:var(--accent); margin:0 0.1em;">·</span>Notes
  </span>
</span>
