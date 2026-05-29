// ============================================================
// EM Notes — Logo + shared atoms
// ============================================================

// Logo concept: a stack of three notation marks (a tally / line marks)
// stylized into the letter "e" → suggests "notes" + "engineering management".
// Variants per direction.

const Logo = ({ size = 28, variant = "mark" }) => {
  // mark only
  if (variant === "mark") {
    return (
      <svg width={size} height={size} viewBox="0 0 40 40" fill="none" aria-label="EM Notes">
        {/* The mark is three horizontal strokes of varying length — like ledger lines / notes,
            with a subtle riser on the right forming a flag. */}
        <rect x="6"  y="9"  width="22" height="4" rx="1" fill="currentColor" />
        <rect x="6"  y="18" width="28" height="4" rx="1" fill="currentColor" />
        <rect x="6"  y="27" width="16" height="4" rx="1" fill="currentColor" />
        {/* Accent dot — the "pen tip" / mark of attention */}
        <circle cx="32" cy="29" r="3" fill="var(--accent)" />
      </svg>
    );
  }
  return null;
};

const Wordmark = ({ size = 22 }) => (
  <span style={{ display: "inline-flex", alignItems: "baseline", gap: 8, color: "var(--fg-1)" }}>
    <Logo size={size + 6} />
    <span
      className="display"
      style={{ fontSize: size, fontWeight: "var(--display-weight)", letterSpacing: "-0.02em" }}
    >
      EM<span style={{ color: "var(--accent)", margin: "0 0.1em" }}>·</span>Notes
    </span>
  </span>
);

// ============================================================
// Icons (inline, hand-tuned for the manager-toolkit feel)
// ============================================================
const Icon = ({ name, size = 18, stroke = 1.7, color = "currentColor" }) => {
  const p = { width: size, height: size, viewBox: "0 0 24 24", fill: "none", stroke: color, strokeWidth: stroke, strokeLinecap: "round", strokeLinejoin: "round" };
  const paths = {
    home:       <><path d="M3 11l9-7 9 7v9a1 1 0 0 1-1 1h-5v-6h-6v6H4a1 1 0 0 1-1-1z"/></>,
    team:       <><circle cx="9" cy="9" r="3"/><circle cx="17" cy="10" r="2.5"/><path d="M3 19c0-3 2.5-5 6-5s6 2 6 5"/><path d="M14 19c0-2 1.5-3.5 4-3.5S22 17 22 19"/></>,
    person:     <><circle cx="12" cy="8" r="3.5"/><path d="M5 20c0-3.5 3-6 7-6s7 2.5 7 6"/></>,
    note:       <><path d="M6 3h9l4 4v14H6z"/><path d="M14 3v5h5"/><path d="M9 13h7M9 17h5"/></>,
    feedback:   <><path d="M21 12a8 8 0 1 1-3-6.2L21 4v6h-6"/></>,
    target:     <><circle cx="12" cy="12" r="8"/><circle cx="12" cy="12" r="4"/><circle cx="12" cy="12" r="1" fill="currentColor"/></>,
    growth:     <><path d="M3 18l5-5 4 3 8-9"/><path d="M14 7h6v6"/></>,
    review:     <><rect x="4" y="4" width="16" height="16" rx="2"/><path d="M8 10h8M8 14h5"/></>,
    actions:    <><path d="M4 7l3 3 6-6"/><path d="M11 13h9M4 17l3 3 6-6"/></>,
    calendar:   <><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 9h18M8 3v4M16 3v4"/></>,
    search:     <><circle cx="11" cy="11" r="6"/><path d="M20 20l-4-4"/></>,
    settings:   <><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.7 1.7 0 0 0 .3 1.9l.1.1a2 2 0 1 1-2.8 2.8l-.1-.1a1.7 1.7 0 0 0-1.9-.3 1.7 1.7 0 0 0-1 1.5V21a2 2 0 1 1-4 0v-.1a1.7 1.7 0 0 0-1-1.5 1.7 1.7 0 0 0-1.9.3l-.1.1a2 2 0 1 1-2.8-2.8l.1-.1A1.7 1.7 0 0 0 4.6 15a1.7 1.7 0 0 0-1.5-1H3a2 2 0 1 1 0-4h.1a1.7 1.7 0 0 0 1.5-1 1.7 1.7 0 0 0-.3-1.9l-.1-.1a2 2 0 1 1 2.8-2.8l.1.1a1.7 1.7 0 0 0 1.9.3h0A1.7 1.7 0 0 0 10 3.1V3a2 2 0 1 1 4 0v.1a1.7 1.7 0 0 0 1 1.5 1.7 1.7 0 0 0 1.9-.3l.1-.1a2 2 0 1 1 2.8 2.8l-.1.1a1.7 1.7 0 0 0-.3 1.9V9a1.7 1.7 0 0 0 1.5 1H21a2 2 0 1 1 0 4h-.1a1.7 1.7 0 0 0-1.5 1z"/></>,
    plus:       <><path d="M12 5v14M5 12h14"/></>,
    chevdown:   <><path d="M6 9l6 6 6-6"/></>,
    chevright:  <><path d="M9 6l6 6-6 6"/></>,
    chevleft:   <><path d="M15 6l-6 6 6 6"/></>,
    check:      <><path d="M5 12l5 5 9-11"/></>,
    sparkles:   <><path d="M12 3l1.6 4.4L18 9l-4.4 1.6L12 15l-1.6-4.4L6 9l4.4-1.6z"/><path d="M19 14l.8 2.2L22 17l-2.2.8L19 20l-.8-2.2L16 17l2.2-.8z"/></>,
    bell:       <><path d="M6 8a6 6 0 1 1 12 0c0 7 3 8 3 8H3s3-1 3-8"/><path d="M10 21a2 2 0 0 0 4 0"/></>,
    cmd:        <><path d="M9 9V6a3 3 0 1 0-3 3h12a3 3 0 1 0-3-3v3M9 9v6m6-6v6m-6 0H6a3 3 0 1 0 3 3v-3m6 0h3a3 3 0 1 1-3 3v-3"/></>,
    pin:        <><path d="M9 4l6 0 1 5 3 2-9 9-2-2 1-3-5-1z"/></>,
    arrow:      <><path d="M5 12h14M13 5l7 7-7 7"/></>,
    clock:      <><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></>,
    flag:       <><path d="M5 21V4M5 4h13l-3 5 3 5H5"/></>,
    book:       <><path d="M4 5a2 2 0 0 1 2-2h13v17H6a2 2 0 0 0-2 2z"/><path d="M4 17a2 2 0 0 1 2-2h13"/></>,
    palette:    <><path d="M12 3a9 9 0 1 0 1 17.9c1-.1 1-1.4.2-2.2-.5-.5-.5-1.3 0-1.8.4-.4 1-.6 1.6-.6H17a4 4 0 0 0 4-4 9 9 0 0 0-9-9z"/><circle cx="7.5" cy="11" r="1" fill="currentColor"/><circle cx="9.5" cy="6.5" r="1" fill="currentColor"/><circle cx="14.5" cy="6.5" r="1" fill="currentColor"/><circle cx="17" cy="11" r="1" fill="currentColor"/></>,
    sun:        <><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M2 12h2M20 12h2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"/></>,
    moon:       <><path d="M21 13a9 9 0 1 1-10-10 7 7 0 0 0 10 10z"/></>,
    pto:        <><path d="M2 17l4-8 4 4 4-2 4 6"/><circle cx="18" cy="6" r="2"/></>,
    trend_up:   <><path d="M3 17l6-6 4 4 8-10"/><path d="M14 5h7v7"/></>,
    trend_down: <><path d="M3 7l6 6 4-4 8 10"/><path d="M14 19h7v-7"/></>,
    star:       <><path d="M12 3l3 6 7 1-5 5 1 7-6-3-6 3 1-7-5-5 7-1z"/></>,
    link:       <><path d="M10 14a4 4 0 0 0 6 0l3-3a4 4 0 0 0-6-6l-1 1"/><path d="M14 10a4 4 0 0 0-6 0l-3 3a4 4 0 0 0 6 6l1-1"/></>,
    archive:    <><rect x="3" y="3" width="18" height="5" rx="1"/><path d="M5 8v12a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1V8M10 12h4"/></>,
    edit:       <><path d="M4 20h4l10-10-4-4L4 16v4z"/><path d="M14 6l4 4"/></>,
    more:       <><circle cx="6" cy="12" r="1.5"/><circle cx="12" cy="12" r="1.5"/><circle cx="18" cy="12" r="1.5"/></>,
    google:     <><path d="M21 12.2c0-.6-.05-1.2-.15-1.8H12v3.6h5.05c-.22 1.15-.88 2.13-1.88 2.78v2.3h3.04c1.78-1.64 2.8-4.06 2.8-6.88z"/><path d="M12 21c2.55 0 4.68-.85 6.23-2.3l-3.04-2.3c-.84.56-1.92.9-3.19.9-2.45 0-4.53-1.65-5.27-3.88H3.6v2.37A9 9 0 0 0 12 21z"/><path d="M6.73 13.42a5.4 5.4 0 0 1 0-3.43V7.62H3.6a9 9 0 0 0 0 8.17z"/><path d="M12 6.16c1.38 0 2.62.47 3.6 1.4l2.7-2.7A9 9 0 0 0 3.6 7.62l3.13 2.37c.73-2.23 2.82-3.83 5.27-3.83z"/></>,
  };
  return <svg {...p}>{paths[name]}</svg>;
};

// ============================================================
// Avatars
// ============================================================
const Avatar = ({ person, size = "md" }) => {
  if (!person) return null;
  return (
    <span className={`avatar avatar-${size}`} style={{ background: person.color, color: "white" }}>
      {person.initials}
    </span>
  );
};

// ============================================================
// Sparkline (sentiment trend)
// ============================================================
const Sparkline = ({ values, width = 80, height = 22, accent = false }) => {
  if (!values || !values.length) return null;
  const max = 5, min = 1;
  const step = width / (values.length - 1);
  const points = values.map((v, i) => [i * step, height - ((v - min) / (max - min)) * (height - 4) - 2]);
  const d = points.map((p, i) => `${i === 0 ? 'M' : 'L'}${p[0].toFixed(1)},${p[1].toFixed(1)}`).join(' ');
  const lastY = points[points.length - 1][1];
  const color = accent ? "var(--accent)" : "var(--fg-2)";
  return (
    <svg width={width} height={height} viewBox={`0 0 ${width} ${height}`} className="sparkline" style={{ display: "block" }}>
      <path d={d} stroke={color} strokeWidth="1.5" fill="none" strokeLinecap="round" strokeLinejoin="round"/>
      <circle cx={width} cy={lastY} r="2.5" fill={color}/>
    </svg>
  );
};

// ============================================================
// Progress bar
// ============================================================
const Bar = ({ value, max = 1, color = "var(--accent)", height = 6 }) => {
  const w = Math.max(0, Math.min(1, value / max)) * 100;
  return (
    <div style={{
      background: "var(--bg-surface-2)", height,
      borderRadius: 999, overflow: "hidden", width: "100%",
    }}>
      <div style={{ width: `${w}%`, height: "100%", background: color, borderRadius: 999, transition: "width 240ms var(--ease)" }}/>
    </div>
  );
};

// ============================================================
// Sentiment dot
// ============================================================
const SentimentDot = ({ score }) => {
  const color = score >= 4 ? "var(--status-ok)" :
                score >= 3 ? "var(--status-warn)" : "var(--status-err)";
  return (
    <span style={{
      display: "inline-flex", alignItems: "center", justifyContent: "center",
      width: 20, height: 20, borderRadius: 999,
      background: `color-mix(in srgb, ${color} 18%, transparent)`,
      color, fontSize: 11, fontWeight: 700, fontFamily: "var(--font-mono)",
    }}>
      {score}
    </span>
  );
};

// ============================================================
// Flag pill (sentiment-drop, promotion-ready, etc.)
// ============================================================
const Flag = ({ kind }) => {
  const cfg = {
    "sentiment-drop":  { label: "Sentiment ↓", cls: "chip-err" },
    "promotion-ready": { label: "Promo-ready", cls: "chip-accent" },
    "new-hire":        { label: "New hire",    cls: "chip-ok" },
    "no-note-7d":      { label: "7d gap",      cls: "chip-warn" },
  }[kind];
  if (!cfg) return null;
  return <span className={`chip ${cfg.cls}`}>{cfg.label}</span>;
};

// ============================================================
// Kbd helper
// ============================================================
const Kbd = ({ children }) => <span className="kbd">{children}</span>;

// ============================================================
// AI card wrapper
// ============================================================
const AICard = ({ title = "AI suggestions", children, dense = false }) => (
  <div className={`card ai-card ${dense ? '' : 'card-pad'}`} style={dense ? { padding: 12 } : {}}>
    <div className="row" style={{ marginBottom: 10, gap: 8 }}>
      <span style={{ color: "var(--accent)", display: "inline-flex" }}>
        <Icon name="sparkles" size={16} />
      </span>
      <span className="eyebrow" style={{ color: "var(--accent)" }}>{title}</span>
    </div>
    {children}
  </div>
);

Object.assign(window, { Logo, Wordmark, Icon, Avatar, Sparkline, Bar, SentimentDot, Flag, Kbd, AICard });
