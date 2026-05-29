// ============================================================
// EM Notes — App shell (sidebar, topbar, routing, command palette)
// ============================================================

const { useState: useStateA, useEffect: useEffectA, useMemo: useMemoA } = React;
const useMemo = useMemoA;

// Defaults persisted between tweaks
const TWEAK_DEFAULS = /*EDITMODE-BEGIN*/{
  "direction": "studio",
  "theme": "light",
  "density": "comfortable",
  "aiOn": true,
  "showPalette": false,
  "accent": "default"
}/*EDITMODE-END*/;

function App() {
  const [t, setT] = useTweaks(TWEAK_DEFAULS);
  const [route, setRoute] = useStateA({ name: "home" });   // home | team | actions | person | settings
  const [paletteOpen, setPaletteOpen] = useStateA(false);
  const [signedIn, setSignedIn] = useStateA(true);
  const [newNoteFor, setNewNoteFor] = useStateA(null);   // person object or null

  const goTo = (name, arg) => {
    if (name === "person") setRoute({ name: "person", personId: arg });
    else setRoute({ name });
    setPaletteOpen(false);
  };

  // Keyboard shortcuts — use refs to avoid stale closure
  const routeRef = React.useRef(route);
  React.useEffect(() => { routeRef.current = route; }, [route]);

  useEffectA(() => {
    let pendingG = false;
    const onKey = (e) => {
      const tag = (e.target.tagName || "").toLowerCase();
      const editing = ["input", "textarea"].includes(tag) || e.target.isContentEditable;
      if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === "k") {
        e.preventDefault(); setPaletteOpen(p => !p); return;
      }
      if (editing) return;
      if (e.key === "/" && !pendingG) { e.preventDefault(); setPaletteOpen(true); return; }
      if (e.key.toLowerCase() === "n" && !pendingG) {
        const r = routeRef.current;
        if (r.name === "person") {
          const p = window.getPerson(r.personId);
          if (p) setNewNoteFor(p);
        } else {
          setPaletteOpen(true);
        }
        return;
      }
      if (e.key.toLowerCase() === "g") { pendingG = true; setTimeout(() => pendingG = false, 800); return; }
      if (pendingG) {
        pendingG = false;
        if (e.key.toLowerCase() === "h") goTo("home");
        else if (e.key.toLowerCase() === "t") goTo("team");
        else if (e.key.toLowerCase() === "s") goTo("settings");
        else if (/^[1-6]$/.test(e.key)) {
          const p = window.EM.TEAM[parseInt(e.key, 10) - 1];
          if (p) goTo("person", p.id);
        }
      }
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, []);

  // Wire body data attributes
  useEffectA(() => {
    document.body.dataset.direction = t.direction;
    document.body.dataset.theme = t.theme;
    document.body.dataset.density = t.density;
  }, [t.direction, t.theme, t.density]);

  if (!signedIn) {
    return <AuthScreen direction={t.direction} onSignIn={() => setSignedIn(true)}/>;
  }

  return (
    <>
      <div className="app">
        <Topbar route={route} goTo={goTo} onOpenPalette={() => setPaletteOpen(true)}/>
        <Sidebar route={route} goTo={goTo}/>
        <main className="app-main">
          {route.name === "home" && <HomeScreen goTo={goTo} aiOn={t.aiOn}/>}
          {route.name === "team" && <TeamScreen goTo={goTo}/>}
          {route.name === "actions" && <ActionsScreen goTo={goTo}/>}
          {route.name === "person" && <PersonScreen personId={route.personId} goTo={goTo} aiOn={t.aiOn} onNewNote={p => setNewNoteFor(p)}/>}
          {route.name === "settings" && <SettingsScreen/>}
        </main>
      </div>

      {paletteOpen && <CommandPalette goTo={goTo} onClose={() => setPaletteOpen(false)} signOut={() => { setSignedIn(false); setPaletteOpen(false); }} onNewNote={(p) => { setNewNoteFor(p); setPaletteOpen(false); }}/>}

      {newNoteFor && <NewNoteModal person={newNoteFor} aiOn={t.aiOn} onClose={() => setNewNoteFor(null)} onSaved={() => setNewNoteFor(null)}/>}

      <TweaksPanel title="Tweaks">
        <TweakSection label="Design direction">
          <TweakRadio
            value={t.direction}
            onChange={v => setT("direction", v)}
            options={[
              { value: "studio", label: "Studio" },
              { value: "conductor", label: "Conductor" },
            ]}
          />
          <div className="meta" style={{ marginTop: 6, fontSize: 11 }}>
            {t.direction === "studio"
              ? "Literary, warm, EbbiWriter-rooted. Rounded corners, serif display."
              : "Manager-toolkit. Squared, slate ink, terracotta accent, mono labels."}
          </div>
        </TweakSection>

        <TweakSection label="Theme">
          <TweakRadio value={t.theme} onChange={v => setT("theme", v)}
            options={[{ value: "light", label: "Light" }, { value: "dark", label: "Dark" }]}/>
        </TweakSection>

        <TweakSection label="Density">
          <TweakRadio value={t.density} onChange={v => setT("density", v)}
            options={[{ value: "comfortable", label: "Comfortable" }, { value: "compact", label: "Compact" }]}/>
        </TweakSection>

        <TweakSection label="AI enhancements">
          <TweakToggle value={t.aiOn} onChange={v => setT("aiOn", v)}
            label="Show AI features"/>
          <div className="meta" style={{ marginTop: 6, fontSize: 11 }}>
            Coaching prompts, auto-summaries, sentiment trends, review drafts.
          </div>
        </TweakSection>

        <TweakSection label="View auth screen">
          <TweakButton onClick={() => setSignedIn(false)} label="Sign out (preview auth)"/>
        </TweakSection>
      </TweaksPanel>
    </>
  );
}

// -----------------------------------------------------------
// TOPBAR
// -----------------------------------------------------------
function Topbar({ route, goTo, onOpenPalette }) {
  return (
    <div className="app-topbar">
      <Wordmark size={18}/>
      <div style={{ flex: 1 }}/>
      <button onClick={onOpenPalette} className="row" style={{
        gap: 8, padding: "6px 12px", borderRadius: "var(--radius-input)",
        background: "var(--bg-surface-2)", border: "1px solid var(--line)",
        color: "var(--fg-3)", cursor: "pointer", fontSize: 13, minWidth: 320,
      }}>
        <Icon name="search" size={14}/>
        <span style={{ flex: 1, textAlign: "left" }}>Search team, notes, actions…</span>
        <Kbd>⌘K</Kbd>
      </button>
      <button className="btn-icon" title="Notifications"><Icon name="bell" size={18}/></button>
      <Avatar person={{ initials: window.EM.ME.initials, color: "var(--accent)" }} size="md"/>
    </div>
  );
}

// -----------------------------------------------------------
// SIDEBAR
// -----------------------------------------------------------
function Sidebar({ route, goTo }) {
  const { TEAM } = window.EM;
  return (
    <aside className="app-sidebar">
      <NavGroup>
        <NavItem icon="home"   label="Home"      active={route.name === "home"}     onClick={() => goTo("home")} shortcut="G H"/>
        <NavItem icon="team"   label="Team"      active={route.name === "team"}     onClick={() => goTo("team")} shortcut="G T" badge={TEAM.length}/>
        <NavItem icon="actions"label="Actions"   active={route.name === "actions"} onClick={() => goTo("actions")} badge={window.EM.OPEN_ACTIONS.length}/>
        <NavItem icon="calendar" label="Calendar" onClick={() => goTo("home")}/>
      </NavGroup>

      <div className="nav-section">Direct reports</div>
      <NavGroup>
        {TEAM.map((p, i) => (
          <NavItem key={p.id}
                   active={route.name === "person" && route.personId === p.id}
                   avatar={p}
                   label={p.name}
                   sub={p.flags.includes("sentiment-drop") ? "↓ sentiment" : p.flags.includes("promotion-ready") ? "promo-ready" : null}
                   badge={p.openActions > 0 ? p.openActions : null}
                   onClick={() => goTo("person", p.id)}
                   shortcut={i < 9 ? `G ${i+1}` : null}/>
        ))}
      </NavGroup>

      <div style={{ flex: 1 }}/>
      <NavGroup>
        <NavItem icon="settings" label="Settings" active={route.name === "settings"} onClick={() => goTo("settings")} shortcut="G S"/>
      </NavGroup>
    </aside>
  );
}

function NavGroup({ children }) {
  return <div className="stack" style={{ ['--stack']: '2px' }}>{children}</div>;
}

function NavItem({ icon, avatar, label, sub, active, onClick, shortcut, badge }) {
  return (
    <button className={`nav-item ${active ? "active" : ""}`} onClick={onClick} title={shortcut ? `Shortcut: ${shortcut}` : ""}>
      {avatar ? <Avatar person={avatar} size="sm"/> : <Icon name={icon} size={16}/>}
      <span style={{ flex: 1, textAlign: "left", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{label}</span>
      {sub && <span style={{ fontSize: 11, color: active ? "rgba(255,255,255,0.7)" : "var(--fg-3)" }}>{sub}</span>}
      {badge != null && <span className="chip" style={{
        background: active ? "rgba(255,255,255,0.15)" : "var(--bg-surface-2)",
        color: active ? "var(--fg-on-dark)" : "var(--fg-3)",
        fontSize: 11, padding: "1px 7px",
      }}>{badge}</span>}
    </button>
  );
}

// -----------------------------------------------------------
// COMMAND PALETTE
// -----------------------------------------------------------
function CommandPalette({ goTo, onClose, signOut, onNewNote }) {
  const { TEAM } = window.EM;
  const [q, setQ] = useStateA("");
  const [idx, setIdx] = useStateA(0);
  const ref = React.useRef(null);
  useEffectA(() => { ref.current?.focus(); }, []);

  const items = useMemo(() => {
    const all = [
      { kind: "nav", label: "Go to Home", icon: "home", run: () => goTo("home") },
      { kind: "nav", label: "Go to Team", icon: "team", run: () => goTo("team") },
      { kind: "nav", label: "Go to Settings", icon: "settings", run: () => goTo("settings") },
      { kind: "action", label: "New 1:1 note…", icon: "plus", run: () => {
          // open submenu by setting query to "1:1" hint OR ask for person — simple approach: pick first
          onNewNote(TEAM[0]);
      }},
      { kind: "action", label: "Log feedback", icon: "feedback", run: () => goTo("team") },
      ...TEAM.map(p => ({ kind: "person", label: p.name, sub: p.role, person: p, run: () => goTo("person", p.id) })),
      ...TEAM.map(p => ({ kind: "action", label: `New 1:1 note → ${p.name}`, icon: "plus", person: p, run: () => onNewNote(p) })),
      { kind: "action", label: "Sign out", icon: "arrow", run: signOut },
    ];
    const ql = q.toLowerCase();
    return ql ? all.filter(it => it.label.toLowerCase().includes(ql) || (it.sub || "").toLowerCase().includes(ql)) : all;
  }, [q]);

  const onKey = (e) => {
    if (e.key === "Escape") { onClose(); return; }
    if (e.key === "ArrowDown") { e.preventDefault(); setIdx(i => Math.min(items.length - 1, i + 1)); }
    if (e.key === "ArrowUp") { e.preventDefault(); setIdx(i => Math.max(0, i - 1)); }
    if (e.key === "Enter") { e.preventDefault(); items[idx]?.run(); }
  };

  return (
    <div className="modal-scrim" onClick={onClose}>
      <div className="modal" style={{ width: 600 }} onClick={e => e.stopPropagation()}>
        <div className="row" style={{ gap: 10, padding: "14px 18px", borderBottom: "1px solid var(--line)" }}>
          <Icon name="search" size={18} color="var(--fg-3)"/>
          <input ref={ref} value={q} onChange={e => { setQ(e.target.value); setIdx(0); }} onKeyDown={onKey}
                 placeholder="Search team, jump to page, run command…"
                 style={{ border: "none", outline: "none", flex: 1, font: "400 16px/1.4 var(--font-ui)", background: "transparent", color: "var(--fg-1)" }}/>
          <Kbd>esc</Kbd>
        </div>
        <div style={{ maxHeight: 400, overflowY: "auto", padding: 8 }}>
          {items.length === 0 && <div className="meta" style={{ padding: 20, textAlign: "center" }}>No matches</div>}
          {items.map((it, i) => (
            <button key={i} onClick={it.run} onMouseEnter={() => setIdx(i)}
                    className="row" style={{
                      width: "100%", padding: "10px 14px",
                      background: i === idx ? "var(--bg-surface-2)" : "transparent",
                      border: "none", borderRadius: "var(--radius-input)",
                      cursor: "pointer", textAlign: "left", gap: 12, color: "var(--fg-1)",
                    }}>
              {it.person ? <Avatar person={it.person} size="sm"/> : <Icon name={it.icon} size={16} color="var(--fg-3)"/>}
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: 14, fontWeight: 500 }}>{it.label}</div>
                {it.sub && <div className="meta">{it.sub}</div>}
              </div>
              <span className="chip chip-draft" style={{ fontSize: 10 }}>{it.kind}</span>
            </button>
          ))}
        </div>
        <div className="row" style={{ gap: 12, padding: "10px 18px", borderTop: "1px solid var(--line)", color: "var(--fg-4)", fontSize: 11 }}>
          <span><Kbd>↑</Kbd> <Kbd>↓</Kbd> navigate</span>
          <span><Kbd>↵</Kbd> select</span>
          <span><Kbd>esc</Kbd> close</span>
        </div>
      </div>
    </div>
  );
}

// Mount
ReactDOM.createRoot(document.getElementById("root")).render(<App/>);
