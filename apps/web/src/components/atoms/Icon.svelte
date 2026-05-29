<script>
  // Inline icon set, hand-tuned for the manager-toolkit feel (ported from atoms.jsx).
  // Inner markup is injected with {@html} so the SVG children render inside the wrapper.
  let { name, size = 18, stroke = 1.7, color = "currentColor" } = $props();

  const paths = {
    home: '<path d="M3 11l9-7 9 7v9a1 1 0 0 1-1 1h-5v-6h-6v6H4a1 1 0 0 1-1-1z"/>',
    team: '<circle cx="9" cy="9" r="3"/><circle cx="17" cy="10" r="2.5"/><path d="M3 19c0-3 2.5-5 6-5s6 2 6 5"/><path d="M14 19c0-2 1.5-3.5 4-3.5S22 17 22 19"/>',
    person: '<circle cx="12" cy="8" r="3.5"/><path d="M5 20c0-3.5 3-6 7-6s7 2.5 7 6"/>',
    note: '<path d="M6 3h9l4 4v14H6z"/><path d="M14 3v5h5"/><path d="M9 13h7M9 17h5"/>',
    feedback: '<path d="M21 12a8 8 0 1 1-3-6.2L21 4v6h-6"/>',
    target: '<circle cx="12" cy="12" r="8"/><circle cx="12" cy="12" r="4"/><circle cx="12" cy="12" r="1" fill="currentColor"/>',
    growth: '<path d="M3 18l5-5 4 3 8-9"/><path d="M14 7h6v6"/>',
    review: '<rect x="4" y="4" width="16" height="16" rx="2"/><path d="M8 10h8M8 14h5"/>',
    actions: '<path d="M4 7l3 3 6-6"/><path d="M11 13h9M4 17l3 3 6-6"/>',
    calendar: '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 9h18M8 3v4M16 3v4"/>',
    search: '<circle cx="11" cy="11" r="6"/><path d="M20 20l-4-4"/>',
    settings:
      '<circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.7 1.7 0 0 0 .3 1.9l.1.1a2 2 0 1 1-2.8 2.8l-.1-.1a1.7 1.7 0 0 0-1.9-.3 1.7 1.7 0 0 0-1 1.5V21a2 2 0 1 1-4 0v-.1a1.7 1.7 0 0 0-1-1.5 1.7 1.7 0 0 0-1.9.3l-.1.1a2 2 0 1 1-2.8-2.8l.1-.1A1.7 1.7 0 0 0 4.6 15a1.7 1.7 0 0 0-1.5-1H3a2 2 0 1 1 0-4h.1a1.7 1.7 0 0 0 1.5-1 1.7 1.7 0 0 0-.3-1.9l-.1-.1a2 2 0 1 1 2.8-2.8l.1.1a1.7 1.7 0 0 0 1.9.3h0A1.7 1.7 0 0 0 10 3.1V3a2 2 0 1 1 4 0v.1a1.7 1.7 0 0 0 1 1.5 1.7 1.7 0 0 0 1.9-.3l.1-.1a2 2 0 1 1 2.8 2.8l-.1.1a1.7 1.7 0 0 0-.3 1.9V9a1.7 1.7 0 0 0 1.5 1H21a2 2 0 1 1 0 4h-.1a1.7 1.7 0 0 0-1.5 1z"/>',
    plus: '<path d="M12 5v14M5 12h14"/>',
    chevdown: '<path d="M6 9l6 6 6-6"/>',
    chevright: '<path d="M9 6l6 6-6 6"/>',
    chevleft: '<path d="M15 6l-6 6 6 6"/>',
    check: '<path d="M5 12l5 5 9-11"/>',
    sparkles:
      '<path d="M12 3l1.6 4.4L18 9l-4.4 1.6L12 15l-1.6-4.4L6 9l4.4-1.6z"/><path d="M19 14l.8 2.2L22 17l-2.2.8L19 20l-.8-2.2L16 17l2.2-.8z"/>',
    bell: '<path d="M6 8a6 6 0 1 1 12 0c0 7 3 8 3 8H3s3-1 3-8"/><path d="M10 21a2 2 0 0 0 4 0"/>',
    cmd: '<path d="M9 9V6a3 3 0 1 0-3 3h12a3 3 0 1 0-3-3v3M9 9v6m6-6v6m-6 0H6a3 3 0 1 0 3 3v-3m6 0h3a3 3 0 1 1-3 3v-3"/>',
    pin: '<path d="M9 4l6 0 1 5 3 2-9 9-2-2 1-3-5-1z"/>',
    arrow: '<path d="M5 12h14M13 5l7 7-7 7"/>',
    clock: '<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>',
    flag: '<path d="M5 21V4M5 4h13l-3 5 3 5H5"/>',
    book: '<path d="M4 5a2 2 0 0 1 2-2h13v17H6a2 2 0 0 0-2 2z"/><path d="M4 17a2 2 0 0 1 2-2h13"/>',
    sun: '<circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M2 12h2M20 12h2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"/>',
    moon: '<path d="M21 13a9 9 0 1 1-10-10 7 7 0 0 0 10 10z"/>',
    pto: '<path d="M2 17l4-8 4 4 4-2 4 6"/><circle cx="18" cy="6" r="2"/>',
    trend_up: '<path d="M3 17l6-6 4 4 8-10"/><path d="M14 5h7v7"/>',
    trend_down: '<path d="M3 7l6 6 4-4 8 10"/><path d="M14 19h7v-7"/>',
    star: '<path d="M12 3l3 6 7 1-5 5 1 7-6-3-6 3 1-7-5-5 7-1z"/>',
    link: '<path d="M10 14a4 4 0 0 0 6 0l3-3a4 4 0 0 0-6-6l-1 1"/><path d="M14 10a4 4 0 0 0-6 0l-3 3a4 4 0 0 0 6 6l1-1"/>',
    archive:
      '<rect x="3" y="3" width="18" height="5" rx="1"/><path d="M5 8v12a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1V8M10 12h4"/>',
    edit: '<path d="M4 20h4l10-10-4-4L4 16v4z"/><path d="M14 6l4 4"/>',
    more: '<circle cx="6" cy="12" r="1.5"/><circle cx="12" cy="12" r="1.5"/><circle cx="18" cy="12" r="1.5"/>',
  };
</script>

<svg
  width={size}
  height={size}
  viewBox="0 0 24 24"
  fill="none"
  stroke={color}
  stroke-width={stroke}
  stroke-linecap="round"
  stroke-linejoin="round"
>
  {@html paths[name] ?? ""}
</svg>
