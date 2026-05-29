// ============================================================
// EM Notes — Actions screen + New 1:1 Note modal
// ============================================================

const { useState: useStateF, useMemo: useMemoF, useEffect: useEffectF, useRef: useRefF } = React;

// =============================================================
// ACTIONS SCREEN
// =============================================================
function ActionsScreen({ goTo }) {
  const all = window.EM.OPEN_ACTIONS;
  const { TEAM } = window.EM;

  const [actions, setActions] = useStateF(all);
  const [filter, setFilter] = useStateF("open");   // open | done | all | mine | theirs
  const [personFilter, setPersonFilter] = useStateF("all");
  const [grouping, setGrouping] = useStateF("due"); // due | person | source
  const [query, setQuery] = useStateF("");

  const toggle = (id) => setActions(prev => prev.map(a => a.id === id ? { ...a, done: !a.done } : a));

  const filtered = useMemoF(() => {
    return actions.filter(a => {
      if (filter === "open" && a.done) return false;
      if (filter === "done" && !a.done) return false;
      if (filter === "mine" && a.owner !== "me") return false;
      if (filter === "theirs" && a.owner !== "report") return false;
      if (personFilter !== "all" && a.person !== personFilter) return false;
      if (query) {
        const q = query.toLowerCase();
        const hay = [a.text, a.from, a.person ? window.getPerson(a.person)?.name : ""].join(" ").toLowerCase();
        if (!hay.includes(q)) return false;
      }
      return true;
    });
  }, [actions, filter, personFilter, query]);

  // Group
  const groups = useMemoF(() => {
    const g = {};
    filtered.forEach(a => {
      let key;
      if (grouping === "person") {
        key = a.person ? window.getPerson(a.person)?.name : "Team-wide";
      } else if (grouping === "source") {
        key = a.from;
      } else {
        // due bucket
        if (a.done) key = "Done";
        else if (a.overdue) key = "Overdue";
        else if (a.due === "Today") key = "Today";
        else if (["This week", "Friday", "Thu", "Wed"].includes(a.due)) key = "This week";
        else key = "Later";
      }
      (g[key] ||= []).push(a);
    });
    // sort order for due grouping
    if (grouping === "due") {
      const order = ["Overdue", "Today", "This week", "Later", "Done"];
      return order.filter(k => g[k]).map(k => [k, g[k]]);
    }
    return Object.entries(g);
  }, [filtered, grouping]);

  const counts = useMemoF(() => ({
    open: actions.filter(a => !a.done).length,
    overdue: actions.filter(a => a.overdue && !a.done).length,
    today: actions.filter(a => a.due === "Today" && !a.done).length,
    done: actions.filter(a => a.done).length,
    mine: actions.filter(a => a.owner === "me" && !a.done).length,
    theirs: actions.filter(a => a.owner === "report" && !a.done).length,
  }), [actions]);

  return (
    <div className="page" data-screen-label="Actions">
      <div className="between" style={{ marginBottom: 24 }}>
        <div>
          <div className="eyebrow">Actions</div>
          <h1 className="display" style={{ fontSize: 36, margin: "4px 0" }}>
            {counts.open} open · <span style={{ color: "var(--status-err)" }}>{counts.overdue} overdue</span>
          </h1>
        </div>
        <div className="row" style={{ gap: 8 }}>
          <button className="btn btn-outline btn-sm"><Icon name="archive" size={14}/> Export</button>
          <button className="btn btn-primary btn-sm" onClick={() => {
            const text = window.prompt("Quick action:");
            if (text) setActions(prev => [{ id: `oa-new-${Date.now()}`, text, person: null, due: "This week", from: "Quick add", owner: "me", done: false }, ...prev]);
          }}><Icon name="plus" size={14}/> Quick add</button>
        </div>
      </div>

      {/* Stat strip */}
      <div className="row" style={{ gap: 0, marginBottom: 18, background: "var(--bg-surface)", border: "1px solid var(--line)", borderRadius: "var(--radius-card)", overflow: "hidden" }}>
        <ActionStat label="Overdue" value={counts.overdue} tone="err" active={filter === "open"} onClick={() => setFilter("open")}/>
        <ActionStat label="Today"   value={counts.today}   tone="warn"/>
        <ActionStat label="Yours"   value={counts.mine}    onClick={() => setFilter("mine")} active={filter === "mine"}/>
        <ActionStat label="Theirs"  value={counts.theirs}  onClick={() => setFilter("theirs")} active={filter === "theirs"}/>
        <ActionStat label="Completed" value={counts.done}  onClick={() => setFilter("done")} active={filter === "done"} last/>
      </div>

      {/* Toolbar */}
      <div className="row" style={{ gap: 12, marginBottom: 16, flexWrap: "wrap" }}>
        <div className="row" style={{
          flex: 1, gap: 8, padding: "8px 12px",
          background: "var(--bg-surface)", border: "1px solid var(--line-strong)",
          borderRadius: "var(--radius-input)", maxWidth: 320,
        }}>
          <Icon name="search" size={16} color="var(--fg-3)"/>
          <input value={query} onChange={e => setQuery(e.target.value)} placeholder="Filter actions…"
            style={{ border: "none", outline: "none", background: "transparent", flex: 1, font: "400 14px/1 var(--font-ui)", color: "var(--fg-1)" }}/>
        </div>

        <div className="row" style={{ gap: 4 }}>
          {[["open", "Open", counts.open], ["mine", "Yours", counts.mine], ["theirs", "Theirs", counts.theirs], ["done", "Done", counts.done], ["all", "All"]].map(([k, label, n]) => (
            <button key={k} onClick={() => setFilter(k)}
                    className={`btn btn-sm ${filter === k ? "btn-primary" : "btn-ghost"}`}>
              {label}{n != null && <span className="mono" style={{ marginLeft: 4, fontSize: 11, opacity: 0.7 }}>{n}</span>}
            </button>
          ))}
        </div>

        <div className="row" style={{ gap: 6, marginLeft: "auto" }}>
          <span className="meta" style={{ fontSize: 12 }}>Group by</span>
          <select value={grouping} onChange={e => setGrouping(e.target.value)}
                  className="input" style={{ width: "auto", padding: "6px 8px", fontSize: 13 }}>
            <option value="due">Due date</option>
            <option value="person">Person</option>
            <option value="source">Source</option>
          </select>
        </div>
      </div>

      {/* Person chips */}
      <div className="row" style={{ gap: 6, marginBottom: 16, flexWrap: "wrap" }}>
        <button className={`chip ${personFilter === "all" ? "chip-accent" : ""}`} style={{ cursor: "pointer" }} onClick={() => setPersonFilter("all")}>
          All people
        </button>
        {TEAM.map(p => {
          const n = actions.filter(a => a.person === p.id && !a.done).length;
          if (n === 0) return null;
          return (
            <button key={p.id} className={`chip ${personFilter === p.id ? "chip-accent" : ""}`} style={{ cursor: "pointer", paddingLeft: 4 }}
                    onClick={() => setPersonFilter(p.id)}>
              <Avatar person={p} size="sm"/>
              {p.name.split(" ")[0]}
              <span className="mono" style={{ opacity: 0.7 }}>{n}</span>
            </button>
          );
        })}
      </div>

      {/* Grouped list */}
      <div className="stack">
        {groups.length === 0 && (
          <div className="card card-pad" style={{ textAlign: "center", padding: 40 }}>
            <div className="display" style={{ fontSize: 22, marginBottom: 6 }}>Nothing here.</div>
            <div className="meta">No actions match the current filter.</div>
          </div>
        )}
        {groups.map(([groupName, items]) => (
          <section key={groupName}>
            <div className="row" style={{ gap: 8, marginBottom: 8, marginTop: 4 }}>
              <div className="eyebrow">{groupName}</div>
              <span className="chip chip-draft" style={{ fontSize: 11 }}>{items.length}</span>
              {groupName === "Overdue" && <span className="dot err" style={{ width: 6, height: 6 }}/>}
            </div>
            <div className="card" style={{ padding: 0, overflow: "hidden" }}>
              {items.map(a => {
                const person = a.person ? window.getPerson(a.person) : null;
                const isOverdue = a.overdue && !a.done;
                return (
                  <div key={a.id} className="list-row" style={{ gridTemplateColumns: "auto 1fr auto auto auto", gap: 14 }}
                       onClick={() => person && goTo("person", person.id)}>
                    <input type="checkbox" checked={a.done} onChange={() => toggle(a.id)}
                           onClick={e => e.stopPropagation()}
                           style={{ width: 18, height: 18, accentColor: "var(--accent)" }}/>
                    <div>
                      <div style={{
                        fontSize: 14, fontWeight: 500,
                        color: a.done ? "var(--fg-4)" : "var(--fg-1)",
                        textDecoration: a.done ? "line-through" : "none",
                      }}>{a.text}</div>
                      <div className="meta" style={{ marginTop: 2, display: "flex", gap: 8, alignItems: "center" }}>
                        <Icon name="link" size={11}/>
                        <span>from {a.from}</span>
                      </div>
                    </div>
                    <div>
                      {person ? (
                        <div className="row" style={{ gap: 6 }}>
                          <Avatar person={person} size="sm"/>
                          <span style={{ fontSize: 13, color: "var(--fg-2)" }}>{person.name.split(" ")[0]}</span>
                        </div>
                      ) : (
                        <span className="chip chip-draft">Team-wide</span>
                      )}
                    </div>
                    <span className="chip" style={{ fontSize: 11 }}>{a.owner === "me" ? "You" : "Report"}</span>
                    <span className={`chip ${isOverdue ? "chip-err" : a.done ? "chip-draft" : a.due === "Today" ? "chip-warn" : "chip-draft"}`}>
                      {a.due}
                    </span>
                  </div>
                );
              })}
            </div>
          </section>
        ))}
      </div>

      <div className="meta" style={{ marginTop: 24, fontSize: 12, textAlign: "center" }}>
        Tip — actions captured in any 1:1 note appear here automatically. <Kbd>X</Kbd> to toggle complete, <Kbd>E</Kbd> to edit.
      </div>
    </div>
  );
}

function ActionStat({ label, value, tone, active, onClick, last }) {
  const color = tone === "err" ? "var(--status-err)" : tone === "warn" ? "var(--status-warn)" : "var(--fg-1)";
  return (
    <button onClick={onClick} disabled={!onClick}
            style={{
              flex: 1, padding: "14px 18px", borderRight: last ? "none" : "1px solid var(--line)",
              background: active ? "var(--bg-surface-2)" : "transparent",
              border: "none", borderRight: last ? "none" : "1px solid var(--line)",
              cursor: onClick ? "pointer" : "default", textAlign: "left",
              transition: "background 120ms var(--ease)",
            }}>
      <div className="eyebrow" style={{ fontSize: 10, marginBottom: 4 }}>{label}</div>
      <div className="display" style={{ fontSize: 24, color }}>{value}</div>
    </button>
  );
}

// =============================================================
// NEW 1:1 NOTE MODAL
// =============================================================
function NewNoteModal({ person, onClose, onSaved, aiOn }) {
  const [tab, setTab] = useStateF("prep"); // prep | notes | actions | wrap
  const [draft, setDraft] = useStateF({
    type: "1:1",
    date: new Date().toISOString().slice(0, 10),
    duration: 30,
    sentiment: 4,
    talkingPoints: [],
    discussion: "",
    actions: [],
    private: "",   // private-only manager notes
  });
  const [actionDraft, setActionDraft] = useStateF("");
  const [transcribing, setTranscribing] = useStateF(false);
  const [carriedDone, setCarriedDone] = useStateF({}); // id -> done

  const update = (k, v) => setDraft(d => ({ ...d, [k]: v }));

  // Pre-populated carry-over items from prior notes + open actions
  const carryOver = useMemoF(() => {
    const all = (window.EM.NOTES[person.id] || []).flatMap(n => n.actions || []);
    return all.filter(a => !a.done).slice(0, 4);
  }, [person.id]);

  const recentFeedback = window.EM.FEEDBACK[person.id]?.slice(0, 2) || [];
  const currentGoals = window.EM.GOALS[person.id] || [];

  // Suggested talking points (smart prompts based on data — not "coaching" suggestions, just surfaced context)
  const suggestedPoints = useMemoF(() => {
    const pts = [];
    carryOver.forEach(a => pts.push({ id: `co-${a.id}`, text: a.text, source: "Carry-over" }));
    recentFeedback.forEach(f => pts.push({ id: `fb-${f.id}`, text: `Discuss recent feedback from ${f.from}`, source: "Feedback" }));
    currentGoals.forEach(g => g.progress < 1 && pts.push({ id: `g-${g.id}`, text: `${g.title} — check progress`, source: "Goal" }));
    if (person.flags.includes("sentiment-drop")) {
      pts.push({ id: "sd", text: "How has the last couple of weeks felt?", source: "Sentiment" });
    }
    if (person.flags.includes("promotion-ready")) {
      pts.push({ id: "pr", text: "Promo packet progress and next steps", source: "Promo" });
    }
    return pts;
  }, [person.id, carryOver, recentFeedback, currentGoals]);

  const togglePoint = (pt) => {
    update("talkingPoints",
      draft.talkingPoints.find(p => p.id === pt.id)
        ? draft.talkingPoints.filter(p => p.id !== pt.id)
        : [...draft.talkingPoints, pt]
    );
  };

  const addAction = () => {
    if (!actionDraft.trim()) return;
    update("actions", [...draft.actions, { id: `na-${Date.now()}`, text: actionDraft.trim(), owner: "me", done: false }]);
    setActionDraft("");
  };

  const removeAction = (id) => update("actions", draft.actions.filter(a => a.id !== id));
  const toggleActionOwner = (id) =>
    update("actions", draft.actions.map(a => a.id === id ? { ...a, owner: a.owner === "me" ? "report" : "me" } : a));

  return (
    <div className="modal-scrim" onClick={onClose}>
      <div className="modal" style={{ width: "94vw", maxWidth: 1080, height: "92vh", display: "flex", flexDirection: "column" }}
           onClick={e => e.stopPropagation()}>
        {/* Header */}
        <div className="between" style={{ padding: "16px 24px", borderBottom: "1px solid var(--line)" }}>
          <div className="row" style={{ gap: 14 }}>
            <Avatar person={person} size="md"/>
            <div>
              <div className="eyebrow">New {draft.type} note</div>
              <div className="display" style={{ fontSize: 22 }}>{person.name}</div>
            </div>
            <div className="row" style={{ gap: 6, marginLeft: 16 }}>
              <select className="input" value={draft.type} onChange={e => update("type", e.target.value)}
                      style={{ width: "auto", padding: "6px 10px", fontSize: 13 }}>
                <option>1:1</option>
                <option>Skip</option>
                <option>Career</option>
                <option>Retro</option>
                <option>Ad-hoc</option>
              </select>
              <input className="input" type="date" value={draft.date} onChange={e => update("date", e.target.value)}
                     style={{ width: "auto", padding: "6px 10px", fontSize: 13 }}/>
              <input className="input" type="number" value={draft.duration} onChange={e => update("duration", parseInt(e.target.value) || 0)}
                     style={{ width: 72, padding: "6px 10px", fontSize: 13 }}/>
              <span className="meta" style={{ fontSize: 12 }}>min</span>
            </div>
          </div>
          <div className="row" style={{ gap: 8 }}>
            <button className={`btn btn-sm ${transcribing ? "btn-accent" : "btn-outline"}`} onClick={() => setTranscribing(t => !t)}>
              <span style={{
                width: 8, height: 8, borderRadius: 999,
                background: transcribing ? "#fff" : "var(--status-err)",
                display: "inline-block",
                animation: transcribing ? "pulse 1.4s infinite" : "none",
              }}/>
              {transcribing ? "Recording…" : "Record"}
            </button>
            <button className="btn btn-ghost btn-sm" onClick={onClose}>Discard</button>
            <button className="btn btn-primary btn-sm" onClick={onSaved}>
              <Icon name="check" size={14}/> Save note
            </button>
          </div>
        </div>

        {/* Tabs */}
        <div style={{ padding: "0 24px", borderBottom: "1px solid var(--line)" }}>
          <div className="tabs" style={{ borderBottom: "none" }}>
            {[
              ["prep",    "Prep",          suggestedPoints.length],
              ["notes",   "Notes",         null],
              ["actions", "Action items",  draft.actions.length || null],
              ["wrap",    "Wrap-up",       null],
            ].map(([k, label, n]) => (
              <button key={k} className={`tab ${tab === k ? "active" : ""}`} onClick={() => setTab(k)}>
                {label}{n != null && <span className="mono" style={{ marginLeft: 6, color: "var(--fg-4)", fontSize: 12 }}>{n}</span>}
              </button>
            ))}
          </div>
        </div>

        {/* Body */}
        <div style={{ flex: 1, overflow: "hidden", display: "grid", gridTemplateColumns: tab === "notes" || tab === "wrap" ? "1fr 320px" : "1fr" }}>
          {/* Main column */}
          <div style={{ overflowY: "auto", padding: "20px 24px" }}>
            {tab === "prep" && (
              <PrepTab person={person} suggestedPoints={suggestedPoints} draft={draft} togglePoint={togglePoint}
                       carryOver={carryOver} carriedDone={carriedDone} setCarriedDone={setCarriedDone}
                       recentFeedback={recentFeedback} currentGoals={currentGoals} aiOn={aiOn}/>
            )}
            {tab === "notes" && (
              <NotesTab draft={draft} update={update} transcribing={transcribing} aiOn={aiOn}/>
            )}
            {tab === "actions" && (
              <ActionsTab draft={draft} actionDraft={actionDraft} setActionDraft={setActionDraft}
                          addAction={addAction} removeAction={removeAction} toggleActionOwner={toggleActionOwner}
                          person={person}/>
            )}
            {tab === "wrap" && (
              <WrapTab draft={draft} update={update} person={person} aiOn={aiOn}/>
            )}
          </div>

          {/* Right rail — context (visible on Notes + Wrap) */}
          {(tab === "notes" || tab === "wrap") && (
            <aside style={{ borderLeft: "1px solid var(--line)", padding: 20, overflowY: "auto", background: "var(--bg-page)" }}>
              <ContextRail person={person} draft={draft} aiOn={aiOn}/>
            </aside>
          )}
        </div>

        {/* Footer */}
        <div className="between" style={{ padding: "12px 24px", borderTop: "1px solid var(--line)", background: "var(--bg-surface)" }}>
          <div className="row" style={{ gap: 14, color: "var(--fg-3)", fontSize: 12 }}>
            <span><Kbd>⌘ ↵</Kbd> save</span>
            <span><Kbd>⌘ /</Kbd> insert template</span>
            <span><Kbd>esc</Kbd> close</span>
          </div>
          <div className="row" style={{ gap: 8 }}>
            {tab !== "prep"    && <button className="btn btn-ghost btn-sm" onClick={() => setTab({notes:"prep", actions:"notes", wrap:"actions"}[tab])}>← Back</button>}
            {tab !== "wrap"    && <button className="btn btn-outline btn-sm" onClick={() => setTab({prep:"notes", notes:"actions", actions:"wrap"}[tab])}>Continue →</button>}
            {tab === "wrap"    && <button className="btn btn-primary btn-sm" onClick={onSaved}><Icon name="check" size={14}/> Save & close</button>}
          </div>
        </div>
      </div>
      <style>{`@keyframes pulse { 0%, 100% { opacity: 1; } 50% { opacity: 0.4; } }`}</style>
    </div>
  );
}

// -----------------------------------------------------------
// PREP TAB
// -----------------------------------------------------------
function PrepTab({ person, suggestedPoints, draft, togglePoint, carryOver, carriedDone, setCarriedDone, recentFeedback, currentGoals, aiOn }) {
  return (
    <div className="stack" style={{ maxWidth: 720 }}>
      <div>
        <div className="display" style={{ fontSize: 22, marginBottom: 4 }}>Build today's agenda</div>
        <div className="meta">Pick from surfaced context, or add your own. Selected items appear in your Notes.</div>
      </div>

      {/* Carry-over from last 1:1 */}
      <section>
        <div className="eyebrow" style={{ marginBottom: 8 }}>Carry-over from last 1:1</div>
        {carryOver.length === 0 ? (
          <div className="card card-pad meta">Nothing carried over.</div>
        ) : (
          <div className="card" style={{ padding: 0 }}>
            {carryOver.map(a => (
              <div key={a.id} className="list-row" style={{ gridTemplateColumns: "auto 1fr auto" }}>
                <input type="checkbox" checked={!!carriedDone[a.id]} onChange={() => setCarriedDone(prev => ({ ...prev, [a.id]: !prev[a.id] }))}
                       style={{ width: 16, height: 16, accentColor: "var(--accent)" }}/>
                <div style={{
                  fontSize: 14,
                  color: carriedDone[a.id] ? "var(--fg-4)" : "var(--fg-1)",
                  textDecoration: carriedDone[a.id] ? "line-through" : "none",
                }}>{a.text}</div>
                <span className="chip chip-draft">{a.owner === "me" ? "You" : person.name.split(" ")[0]}</span>
              </div>
            ))}
          </div>
        )}
      </section>

      {/* Suggested talking points */}
      <section>
        <div className="between" style={{ marginBottom: 8 }}>
          <div className="eyebrow">Surfaced from context</div>
          <span className="meta" style={{ fontSize: 11 }}>{draft.talkingPoints.length} selected</span>
        </div>
        <div className="card" style={{ padding: 0 }}>
          {suggestedPoints.map(pt => {
            const on = !!draft.talkingPoints.find(p => p.id === pt.id);
            return (
              <div key={pt.id} className="list-row" style={{ gridTemplateColumns: "auto 1fr auto", cursor: "pointer" }}
                   onClick={() => togglePoint(pt)}>
                <span style={{
                  width: 18, height: 18, borderRadius: 4,
                  border: `1.5px solid ${on ? "var(--accent)" : "var(--line-strong)"}`,
                  background: on ? "var(--accent)" : "transparent",
                  display: "flex", alignItems: "center", justifyContent: "center",
                  transition: "all 120ms var(--ease)",
                }}>
                  {on && <Icon name="check" size={12} color="#fff"/>}
                </span>
                <div style={{ fontSize: 14, color: "var(--fg-1)" }}>{pt.text}</div>
                <span className="chip chip-draft" style={{ fontSize: 10 }}>{pt.source}</span>
              </div>
            );
          })}
        </div>
      </section>

      {/* Templates */}
      <section>
        <div className="eyebrow" style={{ marginBottom: 8 }}>Insert a template</div>
        <div className="row" style={{ gap: 8, flexWrap: "wrap" }}>
          {[
            ["Standard 1:1",     "Wins · Blockers · Feedback · Growth"],
            ["Career chat",      "Aspirations · Strengths · Gaps · Plan"],
            ["Difficult convo",  "Observation · Impact · Ask · Next"],
            ["Quarterly recap",  "Goals · Highlights · Misses · Theme"],
          ].map(([name, sub]) => (
            <button key={name} className="card hover" style={{ padding: 12, textAlign: "left", cursor: "pointer", minWidth: 200 }}>
              <div style={{ fontSize: 14, fontWeight: 600 }}>{name}</div>
              <div className="meta" style={{ fontSize: 12 }}>{sub}</div>
            </button>
          ))}
        </div>
      </section>
    </div>
  );
}

// -----------------------------------------------------------
// NOTES TAB
// -----------------------------------------------------------
function NotesTab({ draft, update, transcribing, aiOn }) {
  const taRef = useRefF(null);

  // Build the starter notes from selected talking points
  const initialBody = useMemoF(() => {
    if (draft.discussion) return draft.discussion;
    if (draft.talkingPoints.length === 0) return "";
    return draft.talkingPoints.map(p => `## ${p.text}\n\n`).join("\n");
  }, []);

  useEffectF(() => {
    if (!draft.discussion && initialBody) update("discussion", initialBody);
  }, []);

  return (
    <div style={{ maxWidth: 720 }}>
      {transcribing && (
        <div className="row" style={{ gap: 10, padding: "10px 14px", marginBottom: 16, background: "var(--accent-soft)", color: "var(--accent)", borderRadius: "var(--radius-input)", fontSize: 13 }}>
          <span style={{ width: 8, height: 8, borderRadius: 999, background: "var(--accent)", animation: "pulse 1.4s infinite" }}/>
          <span>Listening — transcription appears below in real time.</span>
        </div>
      )}

      <div className="meta" style={{ marginBottom: 8, fontSize: 12 }}>
        Discussion notes — markdown supported. Press <Kbd>/</Kbd> for slash commands.
      </div>
      <textarea
        ref={taRef}
        className="textarea"
        value={draft.discussion}
        onChange={e => update("discussion", e.target.value)}
        placeholder="What did you talk about?"
        style={{ minHeight: 360, fontSize: 15, lineHeight: 1.7, fontFamily: "var(--font-ui)" }}
      />

      <div style={{ marginTop: 24 }}>
        <div className="eyebrow" style={{ marginBottom: 6 }}>Private notes</div>
        <div className="meta" style={{ marginBottom: 6, fontSize: 12 }}>Only visible to you. Not included in auto-summaries shared upward.</div>
        <textarea
          className="textarea"
          value={draft.private}
          onChange={e => update("private", e.target.value)}
          placeholder="Anything you don't want to share more widely…"
          style={{ minHeight: 100, background: "var(--bg-surface-2)", borderColor: "transparent" }}
        />
      </div>
    </div>
  );
}

// -----------------------------------------------------------
// ACTIONS TAB
// -----------------------------------------------------------
function ActionsTab({ draft, actionDraft, setActionDraft, addAction, removeAction, toggleActionOwner, person }) {
  return (
    <div style={{ maxWidth: 720 }}>
      <div className="display" style={{ fontSize: 22, marginBottom: 4 }}>Action items</div>
      <div className="meta" style={{ marginBottom: 16 }}>Each item appears on the Actions board with the right owner.</div>

      <div className="row" style={{ gap: 8, marginBottom: 16 }}>
        <input className="input" value={actionDraft}
               onChange={e => setActionDraft(e.target.value)}
               onKeyDown={e => e.key === "Enter" && addAction()}
               placeholder="Add an action item, e.g. 'Schedule pair on auth refactor'"
               autoFocus/>
        <button className="btn btn-accent" onClick={addAction}><Icon name="plus" size={14}/> Add</button>
      </div>

      {draft.actions.length === 0 ? (
        <div className="card card-pad" style={{ textAlign: "center", padding: 32 }}>
          <div className="meta">No action items yet.</div>
        </div>
      ) : (
        <div className="card" style={{ padding: 0 }}>
          {draft.actions.map(a => (
            <div key={a.id} className="list-row" style={{ gridTemplateColumns: "auto 1fr auto auto", cursor: "default" }}>
              <input type="checkbox" style={{ width: 16, height: 16, accentColor: "var(--accent)" }}/>
              <div style={{ fontSize: 14 }}>{a.text}</div>
              <button className="chip" style={{ cursor: "pointer" }} onClick={() => toggleActionOwner(a.id)}>
                {a.owner === "me" ? "You" : person.name.split(" ")[0]} ↔
              </button>
              <button className="btn-icon" onClick={() => removeAction(a.id)} title="Remove">
                <Icon name="more" size={14}/>
              </button>
            </div>
          ))}
        </div>
      )}

      <div style={{ marginTop: 20 }} className="meta">
        Tip — type "@" in the notes to mention an action inline; it'll be lifted here automatically.
      </div>
    </div>
  );
}

// -----------------------------------------------------------
// WRAP TAB
// -----------------------------------------------------------
function WrapTab({ draft, update, person, aiOn }) {
  return (
    <div className="stack" style={{ maxWidth: 720 }}>
      <div>
        <div className="display" style={{ fontSize: 22, marginBottom: 4 }}>Wrap-up</div>
        <div className="meta">A quick sentiment pulse helps surface trends over time.</div>
      </div>

      <section>
        <div className="eyebrow" style={{ marginBottom: 10 }}>How did this conversation feel?</div>
        <div className="row" style={{ gap: 6 }}>
          {[
            [1, "Concerning"],
            [2, "Tough"],
            [3, "Neutral"],
            [4, "Good"],
            [5, "Energizing"],
          ].map(([score, label]) => (
            <button key={score} onClick={() => update("sentiment", score)}
                    className="card hover"
                    style={{
                      flex: 1, padding: "14px 8px", textAlign: "center",
                      cursor: "pointer",
                      borderColor: draft.sentiment === score ? "var(--accent)" : "var(--line)",
                      borderWidth: draft.sentiment === score ? 2 : 1,
                      background: draft.sentiment === score ? "var(--accent-soft)" : "var(--bg-surface)",
                    }}>
              <div className="display" style={{ fontSize: 22, color: draft.sentiment === score ? "var(--accent)" : "var(--fg-2)" }}>{score}</div>
              <div className="meta" style={{ fontSize: 11, marginTop: 4 }}>{label}</div>
            </button>
          ))}
        </div>
        <div className="meta" style={{ marginTop: 10, fontSize: 12 }}>
          This signal is private to you and used only for sentiment trends.
        </div>
      </section>

      {aiOn && (
        <AICard title="AI summary preview">
          <div style={{ fontSize: 14, color: "var(--fg-1)", lineHeight: 1.6, fontWeight: 500, marginBottom: 8 }}>
            "{draft.talkingPoints[0]?.text || "Solid week"} — {draft.actions.length} action items captured, sentiment {draft.sentiment}/5."
          </div>
          <div className="meta" style={{ fontSize: 12 }}>
            Generated when you save. You can edit before publishing.
          </div>
        </AICard>
      )}

      <section>
        <div className="eyebrow" style={{ marginBottom: 8 }}>Share with</div>
        <div className="card card-pad stack" style={{ ['--stack']: '10px' }}>
          <label className="row" style={{ gap: 10, fontSize: 14 }}>
            <input type="checkbox" defaultChecked style={{ accentColor: "var(--accent)" }}/>
            Share summary with {person.name.split(" ")[0]} (Highlights + Actions only)
          </label>
          <label className="row" style={{ gap: 10, fontSize: 14 }}>
            <input type="checkbox" style={{ accentColor: "var(--accent)" }}/>
            Include in next skip-level digest
          </label>
        </div>
      </section>
    </div>
  );
}

// -----------------------------------------------------------
// CONTEXT RAIL (shown on Notes + Wrap)
// -----------------------------------------------------------
function ContextRail({ person, draft, aiOn }) {
  const last = (window.EM.NOTES[person.id] || [])[0];
  return (
    <div className="stack" style={{ ['--stack']: '20px' }}>
      <div>
        <div className="eyebrow" style={{ marginBottom: 8 }}>Talking points</div>
        {draft.talkingPoints.length === 0 ? (
          <div className="meta" style={{ fontSize: 12 }}>None selected. Pick some on the Prep tab.</div>
        ) : (
          <ul style={{ margin: 0, paddingLeft: 18, color: "var(--fg-2)", fontSize: 13, lineHeight: 1.5 }}>
            {draft.talkingPoints.map(p => <li key={p.id}>{p.text}</li>)}
          </ul>
        )}
      </div>

      {last && (
        <div>
          <div className="eyebrow" style={{ marginBottom: 8 }}>Last 1:1 · {last.date}</div>
          <div className="card card-pad" style={{ background: "var(--bg-surface)", padding: 12 }}>
            <div style={{ fontSize: 13, color: "var(--fg-2)", lineHeight: 1.5 }}>{last.summary.slice(0, 140)}…</div>
          </div>
        </div>
      )}

      <div>
        <div className="eyebrow" style={{ marginBottom: 8 }}>Sentiment · 12 wks</div>
        <div className="card card-pad" style={{ padding: 12 }}>
          <div className="row" style={{ gap: 10, alignItems: "center" }}>
            <SentimentDot score={person.sentiment[person.sentiment.length - 1]}/>
            <Sparkline values={person.sentiment} width={120} height={28} accent={person.flags.includes("sentiment-drop")}/>
          </div>
          <div className="meta" style={{ marginTop: 6, fontSize: 12 }}>{person.sentimentLabel}</div>
        </div>
      </div>

      <div>
        <div className="eyebrow" style={{ marginBottom: 8 }}>Goals · Q2</div>
        <div className="stack" style={{ ['--stack']: '8px' }}>
          {(window.EM.GOALS[person.id] || []).map(g => (
            <div key={g.id}>
              <div style={{ fontSize: 13, fontWeight: 500 }}>{g.title}</div>
              <div style={{ marginTop: 4 }}><Bar value={g.progress} height={4}/></div>
            </div>
          ))}
          {(window.EM.GOALS[person.id] || []).length === 0 && (
            <div className="meta" style={{ fontSize: 12 }}>No goals yet.</div>
          )}
        </div>
      </div>
    </div>
  );
}

Object.assign(window, { ActionsScreen, NewNoteModal });
