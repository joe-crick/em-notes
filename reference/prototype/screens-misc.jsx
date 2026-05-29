// ============================================================
// EM Notes — Feedback, Growth, Review, Settings, Auth
// ============================================================

// -----------------------------------------------------------
// FEEDBACK TAB
// -----------------------------------------------------------
function PersonFeedback({ person, aiOn }) {
  const fb = window.EM.FEEDBACK[person.id] || [];
  const praise = fb.filter(f => f.type === "praise");
  const constructive = fb.filter(f => f.type === "constructive");
  return (
    <div style={{ display: "grid", gridTemplateColumns: "1fr 320px", gap: 24 }}>
      <div className="stack">
        <div className="between">
          <div className="meta">{fb.length} item{fb.length !== 1 && "s"} captured</div>
          <button className="btn btn-accent btn-sm"><Icon name="plus" size={14}/> Log feedback</button>
        </div>
        {fb.map(f => (
          <div key={f.id} className="card card-pad">
            <div className="row" style={{ justifyContent: "space-between", marginBottom: 8 }}>
              <div className="row" style={{ gap: 10 }}>
                <span className={`chip ${f.type === "praise" ? "chip-ok" : "chip-warn"}`}>
                  {f.type === "praise" ? "★ Praise" : "△ Constructive"}
                </span>
                <span className="meta">from <strong style={{ color: "var(--fg-1)" }}>{f.from}</strong></span>
              </div>
              <span className="meta mono">{f.date}</span>
            </div>
            <div style={{ fontSize: 15, lineHeight: 1.6, color: "var(--fg-2)" }}>
              "{f.text}"
            </div>
          </div>
        ))}
        {fb.length === 0 && (
          <div className="card card-pad" style={{ textAlign: "center", padding: 40 }}>
            <div className="display" style={{ fontSize: 22, marginBottom: 6 }}>No feedback captured yet</div>
            <div className="meta">Drop in praise or constructive notes as they happen.</div>
          </div>
        )}
      </div>
      <div className="stack">
        {aiOn && fb.length > 0 && (
          <AICard title="Themes (rolling 90d)">
            <div className="stack" style={{ ['--stack']: '8px' }}>
              <div className="row" style={{ gap: 8 }}>
                <Bar value={0.85} color="var(--status-ok)"/>
                <span style={{ fontSize: 13, minWidth: 110 }}>Communication</span>
              </div>
              <div className="row" style={{ gap: 8 }}>
                <Bar value={0.7} color="var(--status-ok)"/>
                <span style={{ fontSize: 13, minWidth: 110 }}>Technical depth</span>
              </div>
              <div className="row" style={{ gap: 8 }}>
                <Bar value={0.45} color="var(--status-warn)"/>
                <span style={{ fontSize: 13, minWidth: 110 }}>Delegation</span>
              </div>
              <div className="row" style={{ gap: 8 }}>
                <Bar value={0.4} color="var(--status-warn)"/>
                <span style={{ fontSize: 13, minWidth: 110 }}>Strategic vision</span>
              </div>
            </div>
            <div className="meta" style={{ marginTop: 14, fontSize: 12, lineHeight: 1.5 }}>
              Recurring strength: clarity in technical communication.<br/>
              Growth edge: delegating ownership earlier in design reviews.
            </div>
          </AICard>
        )}
        <div className="card card-pad">
          <div className="eyebrow" style={{ marginBottom: 10 }}>Summary</div>
          <div className="row" style={{ gap: 16 }}>
            <div>
              <div className="display" style={{ fontSize: 24 }}>{praise.length}</div>
              <div className="meta">Praise</div>
            </div>
            <div>
              <div className="display" style={{ fontSize: 24 }}>{constructive.length}</div>
              <div className="meta">Constructive</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

// -----------------------------------------------------------
// GROWTH TAB
// -----------------------------------------------------------
function PersonGrowth({ person, aiOn }) {
  const g = window.EM.GROWTH[person.id];
  if (!g) return <div className="card card-pad meta">No growth plan yet.</div>;

  // Radar/spider chart (simple SVG)
  const cx = 160, cy = 160, R = 110;
  const n = g.competencies.length;
  const angle = i => (Math.PI * 2 * i) / n - Math.PI / 2;
  const pt = (i, v) => [cx + Math.cos(angle(i)) * R * v, cy + Math.sin(angle(i)) * R * v];
  const path = vals => vals.map((v, i) => `${i === 0 ? 'M' : 'L'}${pt(i, v).join(',')}`).join(' ') + 'Z';

  return (
    <div style={{ display: "grid", gridTemplateColumns: "1.1fr 1fr", gap: 24 }}>
      <div className="stack">
        <div className="card card-pad">
          <div className="eyebrow" style={{ marginBottom: 6 }}>Ladder</div>
          <div className="row" style={{ gap: 16, alignItems: "center" }}>
            <div>
              <div className="meta">Currently</div>
              <div className="display" style={{ fontSize: 22 }}>{g.currentLevel}</div>
            </div>
            <Icon name="arrow" size={20} color="var(--accent)"/>
            <div>
              <div className="meta">Targeting</div>
              <div className="display" style={{ fontSize: 22 }}>{g.targetLevel}</div>
            </div>
          </div>
          <div className="row" style={{ gap: 12, marginTop: 16, paddingTop: 16, borderTop: "1px solid var(--line)" }}>
            <Icon name="clock" size={16} color="var(--fg-3)"/>
            <span className="meta">Est. timeline: <strong style={{ color: "var(--fg-1)" }}>{g.estimate}</strong></span>
          </div>
        </div>

        <div className="card card-pad">
          <div className="eyebrow" style={{ marginBottom: 14 }}>Competency progress</div>
          <div className="stack" style={{ ['--stack']: '14px' }}>
            {g.competencies.map((c, i) => (
              <div key={i}>
                <div className="between" style={{ marginBottom: 6 }}>
                  <div style={{ fontSize: 14, fontWeight: 500 }}>{c.name}</div>
                  <div className="meta mono">{Math.round(c.current * 100)}% → {Math.round(c.target * 100)}%</div>
                </div>
                <div style={{ position: "relative", height: 8, background: "var(--bg-surface-2)", borderRadius: 999, overflow: "hidden" }}>
                  <div style={{ position: "absolute", left: 0, top: 0, height: "100%", width: `${c.target * 100}%`, background: "var(--bg-mint)" }}/>
                  <div style={{ position: "absolute", left: 0, top: 0, height: "100%", width: `${c.current * 100}%`, background: "var(--accent)" }}/>
                </div>
              </div>
            ))}
          </div>
        </div>

        {aiOn && (
          <AICard title="Growth recommendations">
            <ul style={{ margin: 0, paddingLeft: 18, color: "var(--fg-2)", fontSize: 14, lineHeight: 1.6 }}>
              <li><strong style={{ color: "var(--fg-1)" }}>Cross-team influence</strong> is the biggest gap. Suggest leading the next architecture forum talk.</li>
              <li><strong style={{ color: "var(--fg-1)" }}>Strategic thinking</strong> — pair on the H2 roadmap draft to build muscle.</li>
              <li>Execution is already past target — protect this strength while shifting time toward strategic work.</li>
            </ul>
          </AICard>
        )}
      </div>

      <div className="card card-pad" style={{ display: "flex", flexDirection: "column", alignItems: "center" }}>
        <div className="eyebrow" style={{ alignSelf: "flex-start", marginBottom: 4 }}>Skills profile</div>
        <svg width="320" height="320" viewBox="0 0 320 320">
          {[0.25, 0.5, 0.75, 1.0].map(s => (
            <polygon key={s} points={g.competencies.map((_, i) => pt(i, s).join(",")).join(" ")}
                     fill="none" stroke="var(--line)" strokeWidth="1"/>
          ))}
          {g.competencies.map((_, i) => (
            <line key={i} x1={cx} y1={cy} x2={pt(i, 1)[0]} y2={pt(i, 1)[1]} stroke="var(--line)" strokeWidth="1"/>
          ))}
          {/* Target shape */}
          <path d={path(g.competencies.map(c => c.target))} fill="var(--bg-mint)" fillOpacity="0.4" stroke="var(--bg-mint)" strokeWidth="1.5"/>
          {/* Current shape */}
          <path d={path(g.competencies.map(c => c.current))} fill="var(--accent)" fillOpacity="0.18" stroke="var(--accent)" strokeWidth="2"/>
          {g.competencies.map((c, i) => {
            const [x, y] = pt(i, 1.18);
            return <text key={i} x={x} y={y} fontSize="11" fill="var(--fg-2)" textAnchor="middle" dominantBaseline="middle" style={{ fontFamily: "var(--font-ui)", fontWeight: 500 }}>{c.name}</text>;
          })}
        </svg>
        <div className="row" style={{ gap: 16, marginTop: 8 }}>
          <div className="row" style={{ gap: 6 }}>
            <span style={{ width: 10, height: 10, borderRadius: 2, background: "var(--accent)" }}/>
            <span className="meta">Current</span>
          </div>
          <div className="row" style={{ gap: 6 }}>
            <span style={{ width: 10, height: 10, borderRadius: 2, background: "var(--bg-mint)" }}/>
            <span className="meta">Target</span>
          </div>
        </div>
      </div>
    </div>
  );
}

// -----------------------------------------------------------
// REVIEW PREP TAB
// -----------------------------------------------------------
function PersonReview({ person, aiOn }) {
  return (
    <div style={{ display: "grid", gridTemplateColumns: "1fr 320px", gap: 24 }}>
      <div className="stack">
        <div className="card card-pad">
          <div className="row" style={{ gap: 12, marginBottom: 16, paddingBottom: 16, borderBottom: "1px solid var(--line)" }}>
            <Icon name="review" size={18} color="var(--fg-3)"/>
            <div>
              <div className="eyebrow">Performance review</div>
              <div className="display" style={{ fontSize: 24 }}>Q2 2026 · Draft</div>
            </div>
            <div style={{ flex: 1 }}/>
            <button className="btn btn-outline btn-sm"><Icon name="archive" size={14}/> Export</button>
            <button className="btn btn-primary btn-sm">Save draft</button>
          </div>
          <div className="stack">
            {[
              ["Impact", "Led the idempotency migration end-to-end. Zero incidents on rollout. Cross-team scoping with Infra resolved a contentious partition-key disagreement in <1 day."],
              ["Technical contribution", "Designed and shipped the idempotency keys system. Mentored Jess through onboarding and pairing on payments primitives."],
              ["Collaboration & influence", "Established daily updates pattern during migration. Strong feedback from PM Priya and Infra team."],
              ["Growth edge", "Wants more autonomy in design reviews — should delegate review threads to peers rather than acting as the sole reviewer."],
            ].map(([heading, body]) => (
              <div key={heading}>
                <div className="row" style={{ justifyContent: "space-between" }}>
                  <div className="display" style={{ fontSize: 18 }}>{heading}</div>
                  <button className="btn-icon"><Icon name="edit" size={14}/></button>
                </div>
                <div className="prose" style={{ marginTop: 4 }}>
                  <p>{body}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
      <div className="stack">
        {aiOn && (
          <AICard title="Auto-draft from 1:1s">
            <div style={{ fontSize: 13, color: "var(--fg-2)", lineHeight: 1.55 }}>
              Pulled <strong style={{ color: "var(--fg-1)" }}>12 notes</strong>, <strong style={{ color: "var(--fg-1)" }}>3 feedback items</strong>, and <strong style={{ color: "var(--fg-1)" }}>1 completed goal</strong> from this period.
            </div>
            <button className="btn btn-accent btn-sm" style={{ marginTop: 14, width: "100%", justifyContent: "center" }}>
              <Icon name="sparkles" size={14}/> Regenerate draft
            </button>
          </AICard>
        )}
        <div className="card card-pad">
          <div className="eyebrow" style={{ marginBottom: 10 }}>Sources used</div>
          <div className="stack" style={{ ['--stack']: '6px' }}>
            {[
              ["12 one-on-one notes", "Feb 3 – May 22"],
              ["3 feedback items", "Apr 30 – May 21"],
              ["1 completed goal", "Idempotency migration"],
              ["Sentiment trend", "12-wk window"],
            ].map(([label, sub]) => (
              <div key={label} className="row" style={{ gap: 10, padding: "6px 0" }}>
                <Icon name="link" size={14} color="var(--fg-3)"/>
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 13, fontWeight: 500 }}>{label}</div>
                  <div className="meta">{sub}</div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

// -----------------------------------------------------------
// SETTINGS
// -----------------------------------------------------------
function SettingsScreen() {
  return (
    <div className="page-narrow" data-screen-label="Settings">
      <div className="eyebrow">Settings</div>
      <h1 className="display" style={{ fontSize: 36, margin: "4px 0 24px" }}>Preferences</h1>

      <div className="stack" style={{ ['--stack']: '24px' }}>
        <SettingSection title="Profile" subtitle="How you appear to your team and how notes are signed.">
          <div className="row" style={{ gap: 16, alignItems: "center" }}>
            <Avatar person={{ initials: "JC", color: "var(--accent)" }} size="lg"/>
            <div style={{ flex: 1 }}>
              <input className="input" defaultValue="Joe Crick" style={{ marginBottom: 8 }}/>
              <input className="input" defaultValue="joe@example.com"/>
            </div>
          </div>
        </SettingSection>

        <SettingSection title="Calendar sync" subtitle="Pull 1:1 events automatically and create pre-meeting prep notes.">
          <div className="card card-pad">
            <div className="between">
              <div className="row" style={{ gap: 12 }}>
                <Icon name="google" size={20}/>
                <div>
                  <div style={{ fontWeight: 500 }}>Google Calendar</div>
                  <div className="meta">Connected · joe@example.com</div>
                </div>
              </div>
              <button className="btn btn-outline btn-sm">Disconnect</button>
            </div>
            <div className="divider" style={{ margin: "14px 0" }}/>
            <div className="row" style={{ gap: 12 }}>
              <label className="row" style={{ gap: 8, fontSize: 14 }}>
                <input type="checkbox" defaultChecked style={{ accentColor: "var(--accent)" }}/>
                Auto-create prep notes for 1:1s
              </label>
              <span className="meta">15 min before</span>
            </div>
          </div>
        </SettingSection>

        <SettingSection title="AI features" subtitle="Summaries, sentiment analysis, and review drafts. Notes stay on your account.">
          <div className="card card-pad stack">
            <ToggleRow title="Auto-summarize 1:1 notes" sub="Generates a 2-3 sentence summary at top of each note." on/>
            <ToggleRow title="Sentiment trend analysis" sub="Surfaces drops and patterns; flags people who need attention." on/>
            <ToggleRow title="Feedback theme clustering" sub="Rolling 90-day strength / growth-edge analysis." on/>
            <ToggleRow title="Review draft generation" sub="Auto-drafts perf reviews from 1:1 + feedback history."/>
            <ToggleRow title="Voice-to-note transcription" sub="Record and transcribe 1:1 audio into structured notes."/>
          </div>
        </SettingSection>

        <SettingSection title="Notifications" subtitle="When EM Notes should nudge you.">
          <div className="card card-pad stack">
            <ToggleRow title="Missing 1:1 nudge" sub="Alert if 7+ days pass without a note on a direct report." on/>
            <ToggleRow title="Sentiment drop alert" sub="Flag two-week sentiment declines." on/>
            <ToggleRow title="Action item deadlines" sub="Remind day-of and day-before." on/>
            <ToggleRow title="Weekly digest" sub="Friday 4pm summary of the week." />
          </div>
        </SettingSection>

        <SettingSection title="Keyboard shortcuts" subtitle="EM Notes is keyboard-first.">
          <div className="card card-pad">
            <div className="stack" style={{ ['--stack']: '6px' }}>
              {[
                ["⌘ K", "Command palette"],
                ["G then H", "Go home"],
                ["G then T", "Go to team"],
                ["G then 1-9", "Jump to direct report"],
                ["N", "New 1:1 note"],
                ["F", "Log feedback"],
                ["/", "Search"],
                ["?", "Show shortcuts"],
              ].map(([k, label]) => (
                <div key={k} className="between" style={{ padding: "4px 0" }}>
                  <span style={{ fontSize: 14 }}>{label}</span>
                  <span className="mono"><Kbd>{k}</Kbd></span>
                </div>
              ))}
            </div>
          </div>
        </SettingSection>
      </div>
    </div>
  );
}

function SettingSection({ title, subtitle, children }) {
  return (
    <section>
      <div className="display" style={{ fontSize: 22, marginBottom: 4 }}>{title}</div>
      <div className="meta" style={{ marginBottom: 14 }}>{subtitle}</div>
      {children}
    </section>
  );
}

function ToggleRow({ title, sub, on }) {
  const [val, setVal] = React.useState(!!on);
  return (
    <div className="between" style={{ padding: "8px 0" }}>
      <div style={{ flex: 1 }}>
        <div style={{ fontWeight: 500, fontSize: 14 }}>{title}</div>
        <div className="meta">{sub}</div>
      </div>
      <button onClick={() => setVal(v => !v)}
              style={{
                width: 40, height: 22, borderRadius: 999,
                background: val ? "var(--accent)" : "var(--bg-surface-2)",
                border: "1px solid var(--line-strong)", cursor: "pointer",
                position: "relative", transition: "all 180ms var(--ease)",
              }}>
        <span style={{
          position: "absolute", top: 2, left: val ? 20 : 2,
          width: 16, height: 16, background: "white", borderRadius: 999,
          transition: "left 180ms var(--ease)",
          boxShadow: "0 1px 2px rgba(0,0,0,0.15)",
        }}/>
      </button>
    </div>
  );
}

// -----------------------------------------------------------
// AUTH
// -----------------------------------------------------------
function AuthScreen({ onSignIn, direction }) {
  return (
    <div style={{ minHeight: "100vh", display: "grid", gridTemplateColumns: "1fr 1fr" }}>
      {/* LEFT: copy + form */}
      <div style={{ padding: "80px 64px", display: "flex", flexDirection: "column" }}>
        <Wordmark size={22}/>
        <div style={{ flex: 1 }}/>
        <div style={{ maxWidth: 420 }}>
          <div className="eyebrow" style={{ marginBottom: 10 }}>Sign in</div>
          <h1 className="display" style={{ fontSize: 44, lineHeight: 1.05, margin: "0 0 16px" }}>
            A quieter place for the work of managing people.
          </h1>
          <p style={{ fontSize: 16, color: "var(--fg-2)", lineHeight: 1.6, marginBottom: 32 }}>
            1:1 notes, feedback, goals, and growth — together in one calm system, built for engineering managers.
          </p>
          <button onClick={onSignIn} className="btn btn-primary" style={{ width: "100%", justifyContent: "center", padding: "14px 16px" }}>
            <Icon name="google" size={16}/> Continue with Google
          </button>
          <button className="btn btn-outline" style={{ width: "100%", justifyContent: "center", padding: "14px 16px", marginTop: 10 }}>
            Continue with email
          </button>
          <div className="meta" style={{ marginTop: 18, fontSize: 12 }}>
            Your notes are end-to-end encrypted. AI features can be toggled off entirely in settings.
          </div>
        </div>
        <div style={{ flex: 1 }}/>
        <div className="meta" style={{ fontSize: 12 }}>© 2026 EM Notes · v2.0</div>
      </div>

      {/* RIGHT: editorial preview */}
      <div style={{
        background: direction === "conductor" ? "var(--bg-surface-2)" : "var(--bg-dark)",
        color: direction === "conductor" ? "var(--fg-1)" : "var(--fg-on-dark)",
        padding: "80px 64px", display: "flex", flexDirection: "column", justifyContent: "center",
        position: "relative", overflow: "hidden",
      }}>
        <div style={{ maxWidth: 480, position: "relative", zIndex: 1 }}>
          <div className="eyebrow" style={{ color: direction === "conductor" ? "var(--accent)" : "var(--accent)" }}>What's new</div>
          <h2 className="display" style={{ fontSize: 32, lineHeight: 1.15, margin: "10px 0 28px", color: "inherit" }}>
            Notes that summarize themselves, and trends that surface what to pay attention to.
          </h2>
          <div style={{ background: direction === "conductor" ? "var(--bg-surface)" : "var(--bg-dark-2)", padding: 20, borderRadius: "var(--radius-card)", border: "1px solid var(--line)" }}>
            <div className="row" style={{ gap: 8, marginBottom: 10 }}>
              <Icon name="sparkles" size={14} color="var(--accent)"/>
              <span className="eyebrow" style={{ color: "var(--accent)", fontSize: 10 }}>Weekly briefing</span>
            </div>
            <ul style={{ margin: 0, paddingLeft: 18, fontSize: 14, lineHeight: 1.65, color: "inherit", opacity: 0.92 }}>
              <li><strong>Deepa</strong> — sentiment at 2/5 for two weeks; longest note-gap on the team.</li>
              <li><strong>Sam</strong> — sentiment trending down three weeks; on-call relief lands June 1.</li>
              <li><strong>Marco</strong> — promo packet at 85%, calibration window opens in two weeks.</li>
            </ul>
          </div>
        </div>
        {/* Decorative marks */}
        <svg width="240" height="240" viewBox="0 0 240 240" style={{ position: "absolute", right: -40, bottom: -40, opacity: 0.08 }}>
          <rect x="20" y="40"  width="180" height="20" rx="4" fill="currentColor"/>
          <rect x="20" y="100" width="200" height="20" rx="4" fill="currentColor"/>
          <rect x="20" y="160" width="120" height="20" rx="4" fill="currentColor"/>
        </svg>
      </div>
    </div>
  );
}

Object.assign(window, { PersonFeedback, PersonGoals: window.PersonGoals, PersonGrowth, PersonReview, SettingsScreen, AuthScreen });
