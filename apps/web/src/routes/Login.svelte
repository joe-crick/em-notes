<script>
  import { session, setupPassword, login } from "../lib/stores/session.js";
  import Wordmark from "../components/atoms/Wordmark.svelte";
  import Icon from "../components/atoms/Icon.svelte";

  // Two-column layout ported from the prototype AuthScreen. The prototype's OAuth buttons are
  // replaced with the local single-user password flow (§10): setup on first run, else login.
  let password = $state("");
  let error = $state("");
  let busy = $state(false);

  const isSetup = $derived(!$session.configured);

  async function submit(event) {
    event.preventDefault();
    if (busy) return;
    busy = true;
    error = "";
    const res = isSetup ? await setupPassword(password) : await login(password);
    busy = false;
    if (!res.ok) error = res.error?.message ?? "Something went wrong.";
  }
</script>

<div style="min-height:100vh; display:grid; grid-template-columns:1fr 1fr;">
  <!-- LEFT: copy + password form -->
  <div style="padding:80px 64px; display:flex; flex-direction:column;">
    <Wordmark size={22} />
    <div style="flex:1"></div>
    <div style="max-width:420px; width:100%;">
      <div class="eyebrow" style="margin-bottom:10px;">{isSetup ? "Set up" : "Sign in"}</div>
      <h1 class="display" style="font-size:44px; line-height:1.05; margin:0 0 16px;">
        A quieter place for the work of managing people.
      </h1>
      <p style="font-size:16px; color:var(--fg-2); line-height:1.6; margin-bottom:32px;">
        1:1 notes, feedback, goals, and growth — together in one calm system, built for
        engineering managers.
      </p>

      <form onsubmit={submit}>
        <label class="eyebrow" for="password" style="display:block; margin-bottom:8px;">
          {isSetup ? "Choose a password" : "Password"}
        </label>
        <input
          id="password"
          class="input"
          type="password"
          bind:value={password}
          placeholder={isSetup ? "At least 8 characters" : "Your password"}
          autocomplete={isSetup ? "new-password" : "current-password"}
        />
        {#if error}
          <div class="meta" style="color:var(--status-err); margin-top:10px;">{error}</div>
        {/if}
        <button
          type="submit"
          class="btn btn-primary"
          disabled={busy || !password}
          style="width:100%; justify-content:center; padding:14px 16px; margin-top:16px;"
        >
          <Icon name="arrow" size={16} />
          {busy ? "…" : isSetup ? "Create password & continue" : "Continue"}
        </button>
      </form>

      <div class="meta" style="margin-top:18px; font-size:12px;">
        Your notes are stored locally and protected by this password. AI features can be toggled
        off entirely in settings.
      </div>
    </div>
    <div style="flex:1"></div>
    <div class="meta" style="font-size:12px;">© 2026 EM Notes · v2.0</div>
  </div>

  <!-- RIGHT: editorial preview -->
  <div
    style="background:var(--bg-dark); color:var(--fg-on-dark); padding:80px 64px; display:flex; flex-direction:column; justify-content:center; position:relative; overflow:hidden;"
  >
    <div style="max-width:480px; position:relative; z-index:1;">
      <div class="eyebrow" style="color:var(--accent);">What's new</div>
      <h2 class="display" style="font-size:32px; line-height:1.15; margin:10px 0 28px; color:inherit;">
        Notes that summarize themselves, and trends that surface what to pay attention to.
      </h2>
      <div
        style="background:var(--bg-dark-2); padding:20px; border-radius:var(--radius-card); border:1px solid var(--line);"
      >
        <div class="row" style="gap:8px; margin-bottom:10px;">
          <Icon name="sparkles" size={14} color="var(--accent)" />
          <span class="eyebrow" style="color:var(--accent); font-size:10px;">Weekly briefing</span>
        </div>
        <ul style="margin:0; padding-left:18px; font-size:14px; line-height:1.65; opacity:0.92;">
          <li><strong>Deepa</strong> — sentiment at 2/5 for two weeks; longest note-gap on the team.</li>
          <li><strong>Sam</strong> — sentiment trending down three weeks; on-call relief lands June 1.</li>
          <li><strong>Marco</strong> — promo packet at 85%, calibration window opens in two weeks.</li>
        </ul>
      </div>
    </div>
    <svg
      width="240"
      height="240"
      viewBox="0 0 240 240"
      style="position:absolute; right:-40px; bottom:-40px; opacity:0.08;"
    >
      <rect x="20" y="40" width="180" height="20" rx="4" fill="currentColor" />
      <rect x="20" y="100" width="200" height="20" rx="4" fill="currentColor" />
      <rect x="20" y="160" width="120" height="20" rx="4" fill="currentColor" />
    </svg>
  </div>
</div>
