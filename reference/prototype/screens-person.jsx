// ============================================================
// EM Notes — Person detail (overview + tabs)
// ============================================================
const { useState: useStateP, useMemo: useMemoP } = React;

function PersonScreen({ personId, goTo, aiOn, onNewNote }) {
  const person = window.getPerson(personId);
  const [tab, setTab] = useStateP("overview");
  if (!person) return null;

  return (
    <div className="page" style={{ maxWidth: 1280 }} data-screen-label={`Person · ${person.name}`}>
      {/* Breadcrumb + header */}
      <div className="row" style={{ gap: 6, marginBottom: 20, color: "var(--fg-3)", fontSize: 13 }}>
        <button className="btn btn-ghost btn-sm" onClick={() => goTo("team")} style={{ padding: "4px 8px" }}>
          <Icon name="chevleft" size={14}/> Team
        </button>
        <span>/</span>
        <span style={{ color: "var(--fg-1)", fontWeight: 500 }}>{person.name}</span>
      </div>

      <div className="row" style={{ gap: 20, marginBottom: 28, alignItems: "flex-start" }}>
        <Avatar person={person} size="xl"/>
        <div style={{ flex: 1 }}>
          <div className="row" style={{ gap: 8 }}>
            <h1 className="display" style={{ fontSize: 32, margin: 0 }}>{person.name}</h1>
          </div>
          <div style={{ color: "var(--fg-2)", fontSize: 15, marginTop: 4 }}>
            {person.role} · <span className="mono">{person.level}</span> · {person.tenure} · {person.timezone}
          </div>
          <div className="row" style={{ gap: 6, marginTop: 10, flexWrap: "wrap" }}>
            {person.tags.map(t => <span key={t} className="chip">#{t}</span>)}
            {person.flags.map(f => <Flag key={f} kind={f}/>)}
            {person.pto && <span className="chip chip-warn"><Icon name="pto" size={10}/> {person.pto}</span>}
          </div>
        </div>
        <div className="row" style={{ gap: 8 }}>
          <button className="btn btn-outline btn-sm"><Icon name="calendar" size={14}/> Schedule</button>
          <button className="btn btn-accent btn-sm" onClick={() => onNewNote && onNewNote(person)}><Icon name="plus" size={14}/> New 1:1 note <Kbd>N</Kbd></button>
        </div>
      </div>

      {/* Quick stats strip */}
      <div className="row" style={{ gap: 0, marginBottom: 24, background: "var(--bg-surface)", border: "1px solid var(--line)", borderRadius: "var(--radius-card)", overflow: "hidden" }}>
        <Stat label="Next 1:1" value={person.nextOneOnOne} sub="in 18 hours"/>
        <Stat label="Last note" value={person.lastNote}/>
        <Stat label="Sentiment" value={
          <div className="row" style={{ gap: 8 }}>
            <span className="mono">{person.sentiment[person.sentiment.length - 1]}/5</span>
            <Sparkline values={person.sentiment} width={56} height={18} accent={person.flags.includes("sentiment-drop")}/>
          </div>
        } sub={person.sentimentLabel}/>
        <Stat label="Open actions" value={person.openActions} sub={person.openActions > 3 ? "above threshold" : "manageable"}/>
        <Stat label="Growth" value={`${Math.round(person.growthProgress * 100)}%`} sub={person.growthFocus} last/>
      </div>

      {/* Tabs */}
      <div className="tabs" style={{ marginBottom: 20 }}>
        {[
          ["overview", "Overview"],
          ["notes", "1:1 Notes", window.EM.NOTES[person.id]?.length || 0],
          ["goals", "Goals"],
          ["feedback", "Feedback", window.EM.FEEDBACK[person.id]?.length || 0],
          ["growth", "Growth"],
          ["review", "Review prep"],
        ].map(([k, label, count]) => (
          <button key={k} className={`tab ${tab === k ? "active" : ""}`} onClick={() => setTab(k)}>
            {label}{count != null && <span className="mono" style={{ marginLeft: 6, color: "var(--fg-4)", fontSize: 12 }}>{count}</span>}
          </button>
        ))}
      </div>

      {tab === "overview" && <PersonOverview person={person} aiOn={aiOn} setTab={setTab} onNewNote={onNewNote}/>}
      {tab === "notes" && <PersonNotes person={person} aiOn={aiOn} onNewNote={onNewNote}/>}
      {tab === "goals" && <PersonGoals person={person}/>}
      {tab === "feedback" && <PersonFeedback person={person} aiOn={aiOn}/>}
      {tab === "growth" && <PersonGrowth person={person} aiOn={aiOn}/>}
      {tab === "review" && <PersonReview person={person} aiOn={aiOn}/>}
    </div>
  );
}

function Stat({ label, value, sub, last }) {
  return (
    <div style={{ flex: 1, padding: "14px 18px", borderRight: last ? "none" : "1px solid var(--line)" }}>
      <div className="eyebrow" style={{ fontSize: 10, marginBottom: 4 }}>{label}</div>
      <div style={{ fontSize: 18, fontWeight: 500, color: "var(--fg-1)" }}>{value}</div>
      {sub && <div className="meta" style={{ fontSize: 12, marginTop: 2 }}>{sub}</div>}
    </div>
  );
}

// -----------------------------------------------------------
// OVERVIEW TAB
// -----------------------------------------------------------
function PersonOverview({ person, aiOn, setTab }) {
  const notes = window.EM.NOTES[person.id] || [];
  const recent = notes[0];

  return (
    <div style={{ display: "grid", gridTemplateColumns: "1.5fr 1fr", gap: 24 }}>
      <div className="stack">
        {aiOn && (
          <AICard title={`Recent signals`}>
            <div style={{ fontSize: 14, color: "var(--fg-2)", lineHeight: 1.6 }}>
              Pulled from the last 30 days of notes, feedback, and sentiment.
            </div>
            <div className="stack" style={{ marginTop: 12, ['--stack']: '8px' }}>
              <div className="row" style={{ gap: 10 }}>
                <span className="chip chip-ok" style={{ fontSize: 11 }}>strength</span>
                <span style={{ fontSize: 13, color: "var(--fg-1)" }}>Strong execution — 3 shipped milestones this quarter.</span>
              </div>
              <div className="row" style={{ gap: 10 }}>
                <span className="chip chip-warn" style={{ fontSize: 11 }}>edge</span>
                <span style={{ fontSize: 13, color: "var(--fg-1)" }}>Mentions of being "stretched thin" appear in 2 recent notes.</span>
              </div>
              <div className="row" style={{ gap: 10 }}>
                <span className="chip chip-accent" style={{ fontSize: 11 }}>theme</span>
                <span style={{ fontSize: 13, color: "var(--fg-1)" }}>Repeated interest in TL/cross-team scoping work.</span>
              </div>
            </div>
          </AICard>
        )}

        <SectionHeader icon="note" title="Most recent 1:1" action={<button className="btn btn-ghost btn-sm" onClick={() => setTab("notes")}>View all <Icon name="chevright" size={12}/></button>}/>
        {recent ? (
          <div className="card card-pad">
            <div className="between" style={{ marginBottom: 8 }}>
              <div className="row" style={{ gap: 10 }}>
                <span className="chip">{recent.type}</span>
                <span className="meta">{recent.date} · {recent.duration}</span>
              </div>
              <SentimentDot score={recent.sentiment}/>
            </div>
            <div style={{ fontSize: 15, color: "var(--fg-2)", lineHeight: 1.6, marginBottom: 12 }}>
              {recent.summary}
            </div>
            {recent.highlights?.length > 0 && (
              <ul style={{ margin: 0, paddingLeft: 18, color: "var(--fg-2)", fontSize: 14 }}>
                {recent.highlights.map((h, i) => <li key={i}>{h}</li>)}
              </ul>
            )}
          </div>
        ) : <div className="card card-pad meta">No notes yet.</div>}

        <SectionHeader icon="target" title="Current goals" action={<button className="btn btn-ghost btn-sm" onClick={() => setTab("goals")}>View all <Icon name="chevright" size={12}/></button>}/>
        <div className="stack">
          {(window.EM.GOALS[person.id] || []).map(g => (
            <div key={g.id} className="card card-pad">
              <div className="between">
                <div>
                  <div style={{ fontWeight: 600, color: "var(--fg-1)" }}>{g.title}</div>
                  <div className="meta mono">{g.period}</div>
                </div>
                <span className={`chip ${g.status === "on-track" ? "chip-ok" : g.status === "complete" ? "chip-ok" : "chip-warn"}`}>{g.status}</span>
              </div>
              <div style={{ marginTop: 12 }}><Bar value={g.progress}/></div>
            </div>
          ))}
        </div>
      </div>

      <div className="stack">
        <SectionHeader icon="actions" title="Open actions"/>
        <div className="card">
          {notes.flatMap(n => n.actions || []).filter(a => !a.done).map(a => (
            <div key={a.id} className="list-row" style={{ gridTemplateColumns: "auto 1fr auto" }}>
              <input type="checkbox" style={{ width: 16, height: 16, accentColor: "var(--accent)" }}/>
              <div style={{ fontSize: 14, color: "var(--fg-1)" }}>{a.text}</div>
              <span className="chip chip-draft">{a.owner === "me" ? "You" : person.name.split(" ")[0]}</span>
            </div>
          ))}
        </div>

        <SectionHeader icon="trend_up" title="Sentiment · 12 wks"/>
        <div className="card card-pad">
          <div className="row" style={{ gap: 12, alignItems: "flex-end", justifyContent: "space-between" }}>
            <div>
              <div className="display" style={{ fontSize: 32, lineHeight: 1 }}>
                {person.sentiment[person.sentiment.length - 1]}<span style={{ fontSize: 14, color: "var(--fg-3)" }}>/5</span>
              </div>
              <div className="meta">{person.sentimentLabel}</div>
            </div>
            <Sparkline values={person.sentiment} width={150} height={50} accent={person.flags.includes("sentiment-drop")}/>
          </div>
        </div>

        <SectionHeader icon="growth" title="Growth"/>
        <div className="card card-pad">
          <div className="meta">Currently</div>
          <div style={{ fontWeight: 600, marginBottom: 6 }}>{window.EM.GROWTH[person.id]?.currentLevel}</div>
          <div className="meta" style={{ marginTop: 8 }}>Targeting</div>
          <div style={{ fontWeight: 600 }}>{window.EM.GROWTH[person.id]?.targetLevel}</div>
          <div style={{ marginTop: 12 }}><Bar value={person.growthProgress} color="var(--accent)"/></div>
          <div className="meta mono" style={{ marginTop: 6 }}>est. {window.EM.GROWTH[person.id]?.estimate}</div>
        </div>
      </div>
    </div>
  );
}

// -----------------------------------------------------------
// 1:1 NOTES TAB
// -----------------------------------------------------------
function PersonNotes({ person, aiOn, onNewNote }) {
  const notes = window.EM.NOTES[person.id] || [];
  const [selected, setSelected] = useStateP(notes[0]?.id);
  const note = notes.find(n => n.id === selected) || notes[0];

  return (
    <div style={{ display: "grid", gridTemplateColumns: "260px 1fr", gap: 24 }}>
      <div>
        <button className="btn btn-accent" style={{ width: "100%", justifyContent: "center", marginBottom: 12 }}
                onClick={() => onNewNote && onNewNote(person)}>
          <Icon name="plus" size={14}/> New 1:1 note
        </button>
        <div className="stack" style={{ ['--stack']: '4px' }}>
          {notes.map(n => (
            <button key={n.id} onClick={() => setSelected(n.id)}
                    className="card hover" style={{
                      width: "100%", textAlign: "left", padding: 12,
                      borderColor: selected === n.id ? "var(--fg-1)" : "var(--line)",
                      cursor: "pointer", background: "var(--bg-surface)",
                      borderWidth: selected === n.id ? 2 : 1,
                    }}>
              <div className="between" style={{ marginBottom: 4 }}>
                <span className="mono" style={{ fontSize: 12, color: "var(--fg-3)" }}>{n.date}</span>
                <SentimentDot score={n.sentiment}/>
              </div>
              <div style={{ fontSize: 13, color: "var(--fg-2)", lineHeight: 1.4 }}>
                {n.summary.slice(0, 70)}…
              </div>
              <div className="row" style={{ gap: 4, marginTop: 6 }}>
                <span className="chip" style={{ fontSize: 10 }}>{n.type}</span>
                {n.actions?.length > 0 && <span className="chip chip-draft" style={{ fontSize: 10 }}>{n.actions.length} action{n.actions.length > 1 ? "s" : ""}</span>}
              </div>
            </button>
          ))}
        </div>
      </div>

      {note && (
        <div className="stack">
          <div className="card card-pad">
            <div className="between" style={{ marginBottom: 16, paddingBottom: 16, borderBottom: "1px solid var(--line)" }}>
              <div>
                <div className="row" style={{ gap: 10 }}>
                  <span className="chip">{note.type}</span>
                  <span className="display" style={{ fontSize: 22 }}>{note.date}</span>
                </div>
                <div className="meta" style={{ marginTop: 4 }}>{note.duration} · with {person.name}</div>
              </div>
              <div className="row" style={{ gap: 4 }}>
                <button className="btn-icon" title="Edit"><Icon name="edit" size={16}/></button>
                <button className="btn-icon" title="Pin"><Icon name="pin" size={16}/></button>
                <button className="btn-icon" title="More"><Icon name="more" size={16}/></button>
              </div>
            </div>

            {aiOn && (
              <div style={{ background: "var(--bg-surface-2)", padding: 14, borderRadius: "var(--radius-card)", marginBottom: 20 }}>
                <div className="row" style={{ gap: 8, marginBottom: 6 }}>
                  <Icon name="sparkles" size={14} color="var(--accent)"/>
                  <span className="eyebrow" style={{ color: "var(--accent)", fontSize: 10 }}>AI summary</span>
                </div>
                <div style={{ fontSize: 14, color: "var(--fg-1)", fontWeight: 500, lineHeight: 1.55 }}>
                  {note.summary}
                </div>
              </div>
            )}

            <div className="prose">
              <h2>Highlights</h2>
              <ul>
                {note.highlights.map((h, i) => <li key={i}>{h}</li>)}
              </ul>

              <h2>Discussion</h2>
              <p>
                We started with a quick win recap. The idempotency migration has been a notable accomplishment — clean
                rollout, zero incidents, and a clear pattern for similar projects downstream.
              </p>
              <p>
                The conversation turned to tech-lead readiness. {person.name.split(" ")[0]} feels prepared to formally
                step up in Q3 and asked for more cross-team scoping opportunities.
              </p>
              <blockquote>
                "I want peer review of my designs — not just yours."
              </blockquote>
              <p>
                Agreed to introduce {person.name.split(" ")[0]} to Priya, who's been mentoring others on stakeholder
                management. Followup captured below.
              </p>

              <h2>Action items</h2>
              <ul style={{ listStyle: "none", padding: 0 }}>
                {note.actions.map(a => (
                  <li key={a.id} style={{ padding: "8px 0", display: "flex", gap: 10, alignItems: "flex-start" }}>
                    <input type="checkbox" defaultChecked={a.done} style={{ marginTop: 4, accentColor: "var(--accent)" }}/>
                    <div>
                      <div style={{ textDecoration: a.done ? "line-through" : "none", color: a.done ? "var(--fg-4)" : "var(--fg-1)" }}>{a.text}</div>
                      <div className="meta">Owner: {a.owner === "me" ? "You" : person.name.split(" ")[0]}</div>
                    </div>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

// -----------------------------------------------------------
// GOALS TAB
// -----------------------------------------------------------
function PersonGoals({ person }) {
  const goals = window.EM.GOALS[person.id] || [];
  return (
    <div className="stack">
      <div className="between">
        <div className="meta">{goals.length} goal{goals.length !== 1 && "s"} · Q2 2026</div>
        <button className="btn btn-accent btn-sm"><Icon name="plus" size={14}/> New goal</button>
      </div>
      {goals.map(g => (
        <div key={g.id} className="card card-pad">
          <div className="between" style={{ marginBottom: 16 }}>
            <div>
              <div className="display" style={{ fontSize: 22 }}>{g.title}</div>
              <div className="row" style={{ gap: 8, marginTop: 4 }}>
                <span className="chip mono">{g.period}</span>
                <span className={`chip ${g.status === "on-track" || g.status === "complete" ? "chip-ok" : "chip-warn"}`}>{g.status}</span>
              </div>
            </div>
            <div style={{ textAlign: "right" }}>
              <div className="display" style={{ fontSize: 28 }}>{Math.round(g.progress * 100)}<span style={{ fontSize: 14, color: "var(--fg-3)" }}>%</span></div>
            </div>
          </div>
          <Bar value={g.progress} height={8}/>
          <div className="stack" style={{ marginTop: 18, ['--stack']: '10px' }}>
            {g.keyResults.map((kr, i) => (
              <div key={i} className="row" style={{ gap: 12 }}>
                <Icon name={kr.progress >= 1 ? "check" : "target"} size={14} color={kr.progress >= 1 ? "var(--status-ok)" : "var(--fg-3)"}/>
                <div style={{ flex: 1, fontSize: 14, color: kr.progress >= 1 ? "var(--fg-3)" : "var(--fg-1)", textDecoration: kr.progress >= 1 ? "line-through" : "none" }}>{kr.text}</div>
                <div style={{ width: 80 }}><Bar value={kr.progress} height={4}/></div>
                <div className="mono meta" style={{ width: 36, textAlign: "right" }}>{Math.round(kr.progress * 100)}%</div>
              </div>
            ))}
          </div>
        </div>
      ))}
      {goals.length === 0 && (
        <div className="card card-pad" style={{ textAlign: "center", padding: 40 }}>
          <div className="display" style={{ fontSize: 22, marginBottom: 6 }}>No goals yet</div>
          <div className="meta" style={{ marginBottom: 16 }}>Set up Q2 goals to track progress and surface in 1:1s.</div>
          <button className="btn btn-accent btn-sm"><Icon name="plus" size={14}/> Add first goal</button>
        </div>
      )}
    </div>
  );
}

Object.assign(window, { PersonScreen });
