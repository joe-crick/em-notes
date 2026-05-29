// ============================================================
// EM Notes — Screens (Home, Team, Person, Settings, Auth)
// ============================================================

const { useState, useMemo, useEffect, useRef } = React;

// -----------------------------------------------------------
// HOME / TODAY
// -----------------------------------------------------------
function HomeScreen({ goTo, aiOn }) {
  const { TEAM, ME, TODAY_AGENDA, OPEN_ACTIONS, AI_PROMPTS } = window.EM;

  const flagged = TEAM.filter(p => p.flags.length > 0);
  const dayGreeting = "Good morning";

  return (
    <div className="page" data-screen-label="Home">
      <header style={{ marginBottom: 28 }}>
        <div className="eyebrow">Monday · May 26, 2026 · Week 22</div>
        <h1 className="display" style={{ fontSize: 42, margin: "8px 0 4px" }}>
          {dayGreeting}, {ME.name.split(" ")[0]}.
        </h1>
        <p style={{ color: "var(--fg-3)", fontSize: 16, margin: 0 }}>
          You have <strong style={{ color: "var(--fg-1)" }}>2 one-on-ones</strong> today and{" "}
          <strong style={{ color: "var(--fg-1)" }}>{OPEN_ACTIONS.filter(a => a.urgent).length} actions</strong> needing attention.
        </p>
      </header>

      <div style={{ display: "grid", gridTemplateColumns: "1.4fr 1fr", gap: 24 }}>
        {/* LEFT: agenda */}
        <div className="stack">
          <SectionHeader icon="calendar" title="Today's agenda" action={<button className="btn btn-ghost btn-sm"><Icon name="plus" size={14}/> Add</button>}/>
          <div className="card">
            {TODAY_AGENDA.map((item, i) => {
              const person = item.person ? window.getPerson(item.person) : null;
              return (
                <div key={item.id} className="list-row" style={{ gridTemplateColumns: "60px 1fr auto" }}
                     onClick={() => person && goTo("person", person.id)}>
                  <div className="mono" style={{ fontSize: 13, color: "var(--fg-3)" }}>{item.time}</div>
                  <div className="row" style={{ gap: 12 }}>
                    {person ? <Avatar person={person} size="md"/> : (
                      <span className="avatar avatar-md" style={{ background: "var(--bg-surface-2)", color: "var(--fg-3)" }}>
                        <Icon name="calendar" size={14}/>
                      </span>
                    )}
                    <div>
                      <div style={{ fontWeight: 500, color: "var(--fg-1)" }}>{item.title}</div>
                      <div className="meta">{item.duration} min{item.prep && " · Prep ready"}</div>
                    </div>
                  </div>
                  <div className="row" style={{ gap: 6 }}>
                    {item.prep && <span className="chip chip-accent" style={{ fontSize: 11 }}><Icon name="sparkles" size={10}/> Prep</span>}
                    <Icon name="chevright" size={16} color="var(--fg-4)"/>
                  </div>
                </div>
              );
            })}
          </div>

          {aiOn && (
            <AICard title="Weekly briefing">
              <div style={{ fontSize: 14, color: "var(--fg-2)", lineHeight: 1.6 }}>
                <strong style={{ color: "var(--fg-1)" }}>Three signals</strong> from the last 7 days of notes and feedback:
              </div>
              <ul style={{ margin: "10px 0 0", paddingLeft: 18, color: "var(--fg-2)", fontSize: 14, lineHeight: 1.6 }}>
                <li><strong style={{ color: "var(--fg-1)" }}>Deepa</strong> — sentiment at 2/5 for two weeks; longest note-gap on the team.</li>
                <li><strong style={{ color: "var(--fg-1)" }}>Sam</strong> — sentiment trending down three weeks; on-call relief lands June 1.</li>
                <li><strong style={{ color: "var(--fg-1)" }}>Marco</strong> — promo packet at 85%, calibration window opens in two weeks.</li>
              </ul>
            </AICard>
          )}

          <SectionHeader icon="actions" title="Open action items" badge={OPEN_ACTIONS.filter(a => !a.done).length}
            action={<button className="btn btn-ghost btn-sm" onClick={() => goTo("actions")}>View all <Icon name="chevright" size={12}/></button>}/>
          <div className="card">
            {OPEN_ACTIONS.filter(a => !a.done).slice(0, 6).map((a, i) => {
              const person = a.person ? window.getPerson(a.person) : null;
              return (
                <div key={a.id} className="list-row" style={{ gridTemplateColumns: "auto 1fr auto auto", gap: 12 }}
                     onClick={() => person && goTo("person", person.id)}>
                  <input type="checkbox" style={{ width: 16, height: 16, accentColor: "var(--accent)" }} onClick={e => e.stopPropagation()}/>
                  <div>
                    <div style={{ fontWeight: 500, color: "var(--fg-1)" }}>{a.text}</div>
                    <div className="meta">{person ? person.name : "Team-wide"} · from {a.from}</div>
                  </div>
                  <span className={`chip ${a.urgent ? "chip-err" : "chip-draft"}`}>{a.due}</span>
                  {person ? <Avatar person={person} size="sm"/> : <span className="chip chip-draft">Team</span>}
                </div>
              );
            })}
          </div>
        </div>

        {/* RIGHT: signal column */}
        <div className="stack">
          <SectionHeader icon="bell" title="Needs your attention"/>
          <div className="card card-pad">
            {flagged.length === 0 ? (
              <div className="meta">Nothing flagged. Quiet week.</div>
            ) : (
              <div className="stack" style={{ ['--stack']: '14px' }}>
                {flagged.map(p => (
                  <div key={p.id} className="row" style={{ gap: 12, cursor: "pointer" }} onClick={() => goTo("person", p.id)}>
                    <Avatar person={p} size="md"/>
                    <div style={{ flex: 1 }}>
                      <div className="row" style={{ gap: 6 }}>
                        <span style={{ fontWeight: 600, color: "var(--fg-1)" }}>{p.name}</span>
                        {p.flags.map(f => <Flag key={f} kind={f}/>)}
                      </div>
                      <div className="meta">{p.sentimentLabel} · last note {p.lastNote}</div>
                    </div>
                    <Sparkline values={p.sentiment} width={64} height={20} accent={p.flags.includes("sentiment-drop")}/>
                  </div>
                ))}
              </div>
            )}
          </div>

          <SectionHeader icon="pto" title="This week's PTO"/>
          <div className="card card-pad">
            <div className="row" style={{ gap: 12 }}>
              <Avatar person={window.getPerson("jess")} size="md"/>
              <div style={{ flex: 1 }}>
                <div style={{ fontWeight: 500 }}>Jess Tanaka</div>
                <div className="meta">Out Thursday – Friday</div>
              </div>
            </div>
            <div className="meta" style={{ marginTop: 12, paddingTop: 12, borderTop: "1px solid var(--line)" }}>
              No other team PTO this week.
            </div>
          </div>

          <SectionHeader icon="trend_up" title="Team pulse"/>
          <TeamPulseCard/>
        </div>
      </div>
    </div>
  );
}

function SectionHeader({ icon, title, action, badge }) {
  return (
    <div className="between" style={{ marginTop: 8 }}>
      <div className="row" style={{ gap: 8 }}>
        <Icon name={icon} size={16} color="var(--fg-3)"/>
        <h3 style={{ margin: 0, fontSize: 14, fontWeight: 600, color: "var(--fg-1)", letterSpacing: "-0.005em" }}>{title}</h3>
        {badge != null && <span className="chip chip-draft" style={{ fontSize: 11 }}>{badge}</span>}
      </div>
      {action}
    </div>
  );
}

function TeamPulseCard() {
  const { TEAM } = window.EM;
  const latest = TEAM.map(p => p.sentiment[p.sentiment.length - 1]);
  const avg = (latest.reduce((a, b) => a + b, 0) / latest.length).toFixed(1);
  // Average over 12 weeks
  const weekly = Array.from({ length: 12 }, (_, w) => {
    const sum = TEAM.reduce((acc, p) => acc + p.sentiment[w], 0);
    return sum / TEAM.length;
  });
  return (
    <div className="card card-pad">
      <div className="between" style={{ alignItems: "flex-end", marginBottom: 12 }}>
        <div>
          <div className="display" style={{ fontSize: 32, lineHeight: 1 }}>{avg}<span style={{ fontSize: 16, color: "var(--fg-3)" }}>/5</span></div>
          <div className="meta">Avg team sentiment this week</div>
        </div>
        <span className="chip chip-warn"><Icon name="trend_down" size={12}/> -0.3 vs last wk</span>
      </div>
      <Sparkline values={weekly.map(v => Math.round(v * 10) / 10)} width={280} height={48} accent/>
      <div className="row" style={{ justifyContent: "space-between", marginTop: 6, fontSize: 11, color: "var(--fg-4)", fontFamily: "var(--font-mono)" }}>
        <span>12 wks ago</span><span>now</span>
      </div>
    </div>
  );
}

// -----------------------------------------------------------
// TEAM ROSTER
// -----------------------------------------------------------
function TeamScreen({ goTo }) {
  const { TEAM } = window.EM;
  const [query, setQuery] = useState("");
  const [filter, setFilter] = useState("all");
  const [adding, setAdding] = useState(false);
  const filtered = useMemo(() => {
    return TEAM.filter(p => {
      if (filter === "flagged" && !p.flags.length) return false;
      if (filter === "promo" && !p.flags.includes("promotion-ready")) return false;
      if (filter === "new" && !p.flags.includes("new-hire")) return false;
      if (query && !(p.name + " " + p.role + " " + p.tags.join(" ")).toLowerCase().includes(query.toLowerCase())) return false;
      return true;
    });
  }, [TEAM, query, filter]);

  return (
    <div className="page" data-screen-label="Team">
      <div className="between" style={{ marginBottom: 24 }}>
        <div>
          <div className="eyebrow">Team</div>
          <h1 className="display" style={{ fontSize: 36, margin: "4px 0" }}>Payments Platform · {TEAM.length} people</h1>
        </div>
        <div className="row" style={{ gap: 8 }}>
          <button className="btn btn-outline btn-sm"><Icon name="archive" size={14}/> Export</button>
          <button className="btn btn-primary btn-sm" onClick={() => setAdding(true)}><Icon name="plus" size={14}/> Add report</button>
        </div>
      </div>

      <div className="row" style={{ gap: 12, marginBottom: 18, flexWrap: "wrap" }}>
        <div className="row" style={{
          flex: 1, gap: 8, padding: "8px 12px",
          background: "var(--bg-surface)", border: "1px solid var(--line-strong)",
          borderRadius: "var(--radius-input)", maxWidth: 360,
        }}>
          <Icon name="search" size={16} color="var(--fg-3)"/>
          <input value={query} onChange={e => setQuery(e.target.value)} placeholder="Search team, tag, role…"
            style={{ border: "none", outline: "none", background: "transparent", flex: 1, font: "400 14px/1 var(--font-ui)", color: "var(--fg-1)" }}/>
          <Kbd>/</Kbd>
        </div>
        <div className="row" style={{ gap: 4 }}>
          {[["all", "All"], ["flagged", "Flagged"], ["promo", "Promo-ready"], ["new", "New hires"]].map(([k, label]) => (
            <button key={k} onClick={() => setFilter(k)}
                    className={`btn btn-sm ${filter === k ? "btn-primary" : "btn-ghost"}`}>{label}</button>
          ))}
        </div>
      </div>

      <div className="card" style={{ padding: 0, overflow: "hidden" }}>
        {/* Header row */}
        <div className="list-row" style={{ gridTemplateColumns: "2fr 1.2fr 1fr 1.2fr 0.8fr 1fr 0.6fr", cursor: "default", background: "var(--bg-surface-2)", borderBottom: "1px solid var(--line-strong)", padding: "10px 16px" }}>
          {["Name", "Role · Level", "Next 1:1", "Growth focus", "Sentiment", "Flags", "Open"].map(h => (
            <div key={h} className="eyebrow" style={{ fontSize: 10 }}>{h}</div>
          ))}
        </div>
        {filtered.map(p => (
          <div key={p.id} className="list-row" style={{ gridTemplateColumns: "2fr 1.2fr 1fr 1.2fr 0.8fr 1fr 0.6fr" }}
               onClick={() => goTo("person", p.id)}>
            <div className="row" style={{ gap: 12 }}>
              <Avatar person={p} size="md"/>
              <div>
                <div style={{ fontWeight: 600, color: "var(--fg-1)" }}>{p.name}</div>
                <div className="meta">{p.timezone}</div>
              </div>
            </div>
            <div>
              <div style={{ fontSize: 14 }}>{p.role}</div>
              <div className="meta mono">{p.level} · {p.tenure}</div>
            </div>
            <div className="mono" style={{ fontSize: 13 }}>{p.nextOneOnOne}</div>
            <div style={{ fontSize: 13, color: "var(--fg-2)" }}>
              {p.growthFocus}
              <div style={{ marginTop: 4, width: 100 }}><Bar value={p.growthProgress}/></div>
            </div>
            <div><Sparkline values={p.sentiment} width={64} height={20} accent={p.flags.includes("sentiment-drop")}/></div>
            <div className="row" style={{ gap: 4, flexWrap: "wrap" }}>
              {p.flags.map(f => <Flag key={f} kind={f}/>)}
              {p.pto && <span className="chip"><Icon name="pto" size={10}/> PTO</span>}
            </div>
            <div className="mono" style={{ fontSize: 13, color: p.openActions > 3 ? "var(--status-warn)" : "var(--fg-3)" }}>{p.openActions}</div>
          </div>
        ))}
      </div>

      {adding && <AddReportModal onClose={() => setAdding(false)} onSaved={() => setAdding(false)} goTo={goTo}/>}
    </div>
  );
}

// -----------------------------------------------------------
// ADD REPORT MODAL
// -----------------------------------------------------------
function AddReportModal({ onClose, onSaved, goTo }) {
  const [step, setStep] = useState("source"); // source | form | success
  const [draft, setDraft] = useState({
    name: "", email: "", role: "", level: "L4",
    timezone: "PST", startDate: "", reason: "new-hire",
    tags: "", scheduleFirst: true, sendWelcome: true,
  });

  const update = (k, v) => setDraft(d => ({ ...d, [k]: v }));

  // Mocked "directory" candidates
  const directory = [
    { name: "Priya Shah",     email: "priya.shah@example.com",   role: "Engineer II",      level: "L4", timezone: "PST" },
    { name: "Tomás Oliveira", email: "tomas.o@example.com",      role: "Senior Engineer",  level: "L5", timezone: "EST" },
    { name: "Hana Lindberg",  email: "hana.l@example.com",       role: "Engineer I",       level: "L3", timezone: "CET" },
  ];

  return (
    <div className="modal-scrim" onClick={onClose}>
      <div className="modal" style={{ width: 620, maxHeight: "90vh", display: "flex", flexDirection: "column" }} onClick={e => e.stopPropagation()}>
        {/* Header */}
        <div className="between" style={{ padding: "18px 22px", borderBottom: "1px solid var(--line)" }}>
          <div>
            <div className="eyebrow">{step === "success" ? "Done" : "Add direct report"}</div>
            <div className="display" style={{ fontSize: 22, marginTop: 2 }}>
              {step === "source"  && "Who's joining your team?"}
              {step === "form"    && (draft.name || "New direct report")}
              {step === "success" && `${draft.name || "Your new report"} is set up`}
            </div>
          </div>
          <button className="btn-icon" onClick={onClose} title="Close (Esc)" style={{ fontSize: 18 }}>×</button>
        </div>

        {/* Body */}
        <div style={{ padding: 22, overflowY: "auto", flex: 1 }}>
          {step === "source" && (
            <div className="stack">
              <div className="meta" style={{ marginBottom: 4 }}>Choose a starting point.</div>

              <button className="card hover" style={{ padding: 16, width: "100%", textAlign: "left", background: "var(--bg-surface)", cursor: "pointer" }}
                      onClick={() => setStep("form")}>
                <div className="row" style={{ gap: 14 }}>
                  <div style={{ width: 36, height: 36, borderRadius: "var(--radius-input)", background: "var(--accent-soft)", color: "var(--accent)", display: "flex", alignItems: "center", justifyContent: "center" }}>
                    <Icon name="plus" size={18}/>
                  </div>
                  <div style={{ flex: 1 }}>
                    <div style={{ fontWeight: 600, fontSize: 15 }}>Start blank</div>
                    <div className="meta">Fill in details manually.</div>
                  </div>
                  <Icon name="chevright" size={16} color="var(--fg-4)"/>
                </div>
              </button>

              <div className="eyebrow" style={{ marginTop: 10 }}>Or pull from your directory</div>
              <div className="card" style={{ padding: 0 }}>
                {directory.map((d, i) => (
                  <div key={d.email} className="list-row" style={{ gridTemplateColumns: "auto 1fr auto auto", padding: "12px 16px" }}
                       onClick={() => {
                         setDraft(prev => ({ ...prev, name: d.name, email: d.email, role: d.role, level: d.level, timezone: d.timezone }));
                         setStep("form");
                       }}>
                    <Avatar person={{ initials: d.name.split(" ").map(s => s[0]).join("").slice(0, 2), color: "var(--bg-mint)" }} size="md"/>
                    <div>
                      <div style={{ fontWeight: 500 }}>{d.name}</div>
                      <div className="meta">{d.email}</div>
                    </div>
                    <div className="meta mono" style={{ textAlign: "right" }}>{d.role}<br/>{d.level} · {d.timezone}</div>
                    <Icon name="chevright" size={16} color="var(--fg-4)"/>
                  </div>
                ))}
              </div>

              <div className="row" style={{ gap: 12, marginTop: 12, padding: "12px 14px", background: "var(--bg-surface-2)", borderRadius: "var(--radius-input)" }}>
                <Icon name="link" size={16} color="var(--fg-3)"/>
                <div style={{ flex: 1, fontSize: 13, color: "var(--fg-2)" }}>
                  Or invite by email — they'll appear once they accept.
                </div>
                <button className="btn btn-outline btn-sm">Invite by email</button>
              </div>
            </div>
          )}

          {step === "form" && (
            <div className="stack">
              {/* Identity */}
              <FieldRow label="Name">
                <input className="input" value={draft.name} onChange={e => update("name", e.target.value)} placeholder="First Last" autoFocus/>
              </FieldRow>
              <FieldRow label="Work email">
                <input className="input" type="email" value={draft.email} onChange={e => update("email", e.target.value)} placeholder="name@company.com"/>
              </FieldRow>

              {/* Role + level */}
              <div className="row" style={{ gap: 12, alignItems: "stretch" }}>
                <FieldRow label="Role" style={{ flex: 2 }}>
                  <input className="input" value={draft.role} onChange={e => update("role", e.target.value)} placeholder="Senior Engineer"/>
                </FieldRow>
                <FieldRow label="Level" style={{ flex: 1 }}>
                  <select className="input" value={draft.level} onChange={e => update("level", e.target.value)}>
                    {["L3", "L4", "L5", "L6", "L7"].map(l => <option key={l}>{l}</option>)}
                  </select>
                </FieldRow>
                <FieldRow label="Timezone" style={{ flex: 1 }}>
                  <select className="input" value={draft.timezone} onChange={e => update("timezone", e.target.value)}>
                    {["PST", "EST", "GMT", "CET", "IST", "JST"].map(t => <option key={t}>{t}</option>)}
                  </select>
                </FieldRow>
              </div>

              {/* Context */}
              <FieldRow label="Start date">
                <input className="input" type="date" value={draft.startDate} onChange={e => update("startDate", e.target.value)}/>
              </FieldRow>

              <FieldRow label="Joining as">
                <div className="row" style={{ gap: 4, flexWrap: "wrap" }}>
                  {[
                    ["new-hire",   "New hire"],
                    ["transfer",   "Internal transfer"],
                    ["return",     "Returning"],
                    ["contractor", "Contractor"],
                  ].map(([k, label]) => (
                    <button key={k} onClick={() => update("reason", k)}
                            className={`btn btn-sm ${draft.reason === k ? "btn-primary" : "btn-outline"}`}>{label}</button>
                  ))}
                </div>
              </FieldRow>

              <FieldRow label="Tags" hint="Comma-separated. Used for filtering and search.">
                <input className="input" value={draft.tags} onChange={e => update("tags", e.target.value)} placeholder="payments, promo-candidate"/>
              </FieldRow>

              {/* Day-one setup */}
              <div className="card card-pad" style={{ background: "var(--bg-surface-2)", borderColor: "transparent" }}>
                <div className="eyebrow" style={{ marginBottom: 12 }}>Day-one setup</div>
                <div className="stack" style={{ ['--stack']: '10px' }}>
                  <Checkbox checked={draft.scheduleFirst} onChange={v => update("scheduleFirst", v)}
                            label="Schedule first 1:1"
                            sub="30 min, weekly, starting their first Tuesday."/>
                  <Checkbox checked={draft.sendWelcome} onChange={v => update("sendWelcome", v)}
                            label="Share onboarding doc"
                            sub="Sends your team's welcome template to their email."/>
                  <Checkbox checked={false} onChange={() => {}}
                            label="Add to skip-level digest"
                            sub="Include in monthly summary sent to your manager."/>
                </div>
              </div>
            </div>
          )}

          {step === "success" && (
            <div style={{ textAlign: "center", padding: "20px 10px 8px" }}>
              <div style={{
                width: 64, height: 64, margin: "0 auto 16px",
                borderRadius: 999, background: "color-mix(in srgb, var(--status-ok) 18%, transparent)",
                color: "var(--status-ok)", display: "flex", alignItems: "center", justifyContent: "center",
              }}>
                <Icon name="check" size={28}/>
              </div>
              <div className="display" style={{ fontSize: 22, marginBottom: 6 }}>
                {draft.name || "New report"} added to Payments Platform.
              </div>
              <div className="meta" style={{ marginBottom: 22 }}>
                {draft.scheduleFirst && "Calendar invite sent. "}
                {draft.sendWelcome && "Onboarding doc shared."}
                {!draft.scheduleFirst && !draft.sendWelcome && "You can set up their first 1:1 anytime."}
              </div>
              <div className="row" style={{ gap: 8, justifyContent: "center" }}>
                <button className="btn btn-outline btn-sm" onClick={() => { setDraft(d => ({ ...d, name: "", email: "" })); setStep("source"); }}>
                  Add another
                </button>
                <button className="btn btn-primary btn-sm" onClick={onClose}>Done</button>
              </div>
            </div>
          )}
        </div>

        {/* Footer */}
        {step !== "success" && (
          <div className="between" style={{ padding: "14px 22px", borderTop: "1px solid var(--line)", background: "var(--bg-surface)" }}>
            <div className="meta" style={{ fontSize: 12 }}>
              {step === "source" ? "Direct reports can also be added by invite link." : "All fields except name can be edited later."}
            </div>
            <div className="row" style={{ gap: 8 }}>
              {step === "form" && <button className="btn btn-ghost btn-sm" onClick={() => setStep("source")}>← Back</button>}
              <button className="btn btn-ghost btn-sm" onClick={onClose}>Cancel</button>
              {step === "form" && (
                <button className="btn btn-accent btn-sm"
                        disabled={!draft.name}
                        style={{ opacity: draft.name ? 1 : 0.5, pointerEvents: draft.name ? "auto" : "none" }}
                        onClick={() => setStep("success")}>
                  Add to team
                </button>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

function FieldRow({ label, hint, children, style }) {
  return (
    <div style={style}>
      <label className="eyebrow" style={{ display: "block", marginBottom: 6 }}>{label}</label>
      {children}
      {hint && <div className="meta" style={{ marginTop: 4, fontSize: 12 }}>{hint}</div>}
    </div>
  );
}

function Checkbox({ checked, onChange, label, sub }) {
  return (
    <label className="row" style={{ gap: 12, alignItems: "flex-start", cursor: "pointer" }}>
      <input type="checkbox" checked={checked} onChange={e => onChange(e.target.checked)}
             style={{ marginTop: 3, accentColor: "var(--accent)", width: 16, height: 16 }}/>
      <div>
        <div style={{ fontSize: 14, fontWeight: 500, color: "var(--fg-1)" }}>{label}</div>
        {sub && <div className="meta">{sub}</div>}
      </div>
    </label>
  );
}

Object.assign(window, { HomeScreen, TeamScreen, SectionHeader });
