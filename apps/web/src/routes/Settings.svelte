<script>
  import { settings, updateSettings } from "../lib/stores/settings.js";
  import { feeds, addFeed, removeFeed, syncFeeds } from "../lib/stores/calendar.js";
  import { ME } from "../lib/manager.js";
  import Avatar from "../components/atoms/Avatar.svelte";
  import Icon from "../components/atoms/Icon.svelte";
  import Kbd from "../components/atoms/Kbd.svelte";

  // Calendar feeds
  let feedUrl = $state("");
  let feedLabel = $state("");
  let feedBusy = $state(false);
  let feedError = $state("");

  async function submitFeed() {
    if (!feedUrl.trim() || feedBusy) return;
    feedBusy = true;
    feedError = "";
    const res = await addFeed(feedUrl.trim(), feedLabel.trim());
    feedBusy = false;
    if (res.ok) {
      feedUrl = "";
      feedLabel = "";
    } else {
      feedError = res.error?.message ?? "Could not add feed.";
    }
  }

  let syncing = $state(false);
  async function refreshFeeds() {
    syncing = true;
    await syncFeeds();
    syncing = false;
  }

  function syncedLabel(iso) {
    if (!iso) return "never synced";
    return `synced ${new Intl.DateTimeFormat("en-US", { month: "short", day: "numeric", hour: "numeric", minute: "2-digit" }).format(new Date(iso))}`;
  }

  const themes = [
    ["light", "Light", "sun"],
    ["dark", "Dark", "moon"],
  ];
  const densities = [
    ["comfortable", "Comfortable"],
    ["compact", "Compact"],
  ];

  // Team name is editable + persisted; commit on blur/Enter, ignoring a blank value (the
  // contract requires non-empty) and no-op edits.
  let teamDraft = $state($settings.teamName);
  $effect(() => {
    teamDraft = $settings.teamName;
  });

  function commitTeamName() {
    const name = teamDraft.trim();
    if (!name || name === $settings.teamName) {
      teamDraft = $settings.teamName;
      return;
    }
    updateSettings({ teamName: name });
  }

  const shortcuts = [
    ["⌘ K", "Command palette"],
    ["G then H", "Go home"],
    ["G then T", "Go to team"],
    ["G then 1-9", "Jump to direct report"],
    ["N", "New 1:1 note"],
    ["/", "Search"],
    ["?", "Show shortcuts"],
  ];
</script>

<div class="page-narrow" data-screen-label="Settings">
  <div class="eyebrow">Settings</div>
  <h1 class="display" style="font-size:36px; margin:4px 0 24px;">Preferences</h1>

  <div class="stack" style="--stack:24px;">
    <!-- Profile -->
    <section>
      <div class="display" style="font-size:22px; margin-bottom:4px;">Profile</div>
      <div class="meta" style="margin-bottom:14px;">How you appear in this workspace.</div>
      <div class="card card-pad">
        <div class="row" style="gap:16px; align-items:center;">
          <Avatar person={{ initials: ME.initials, color: ME.color }} size="lg" />
          <div style="flex:1;">
            <div style="font-weight:600; font-size:15px;">{ME.name}</div>
            <div class="meta">{ME.role} · {$settings.teamName}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Team -->
    <section>
      <div class="display" style="font-size:22px; margin-bottom:4px;">Team</div>
      <div class="meta" style="margin-bottom:14px;">The workspace name shown on the Team page.</div>
      <div class="card card-pad">
        <label class="eyebrow" for="team-name" style="display:block; margin-bottom:6px;">Team name</label>
        <input
          id="team-name"
          class="input"
          bind:value={teamDraft}
          onblur={commitTeamName}
          onkeydown={(e) => e.key === "Enter" && e.currentTarget.blur()}
          placeholder="e.g. Payments Platform"
        />
      </div>
    </section>

    <!-- Appearance -->
    <section>
      <div class="display" style="font-size:22px; margin-bottom:4px;">Appearance</div>
      <div class="meta" style="margin-bottom:14px;">Theme and density apply across the app and are saved to your local database.</div>
      <div class="card card-pad stack" style="--stack:16px;">
        <div class="between">
          <div>
            <div style="font-weight:500; font-size:14px;">Theme</div>
            <div class="meta">Light or dark palette.</div>
          </div>
          <div class="row" style="gap:4px;">
            {#each themes as [val, label, icon] (val)}
              <button
                class="btn btn-sm {$settings.theme === val ? 'btn-primary' : 'btn-outline'}"
                onclick={() => updateSettings({ theme: val })}
              >
                <Icon name={icon} size={14} /> {label}
              </button>
            {/each}
          </div>
        </div>
        <div class="divider"></div>
        <div class="between">
          <div>
            <div style="font-weight:500; font-size:14px;">Density</div>
            <div class="meta">Spacing of rows and cards.</div>
          </div>
          <div class="row" style="gap:4px;">
            {#each densities as [val, label] (val)}
              <button
                class="btn btn-sm {$settings.density === val ? 'btn-primary' : 'btn-outline'}"
                onclick={() => updateSettings({ density: val })}
              >
                {label}
              </button>
            {/each}
          </div>
        </div>
      </div>
    </section>

    <!-- Calendar feeds -->
    <section>
      <div class="between" style="margin-bottom:4px;">
        <div class="display" style="font-size:22px;">Calendar</div>
        <button class="btn btn-outline btn-sm" onclick={refreshFeeds} disabled={syncing || $feeds.length === 0}>
          <Icon name="trend_up" size={14} /> {syncing ? "Refreshing…" : "Refresh"}
        </button>
      </div>
      <div class="meta" style="margin-bottom:14px;">
        Subscribe to a read-only .ics feed (the "secret iCal address" from Google/Outlook/Apple).
        Events appear on Home, match to reports by attendee email, and seed prep notes.
      </div>

      <div class="card card-pad stack" style="--stack:14px;">
        {#if $feeds.length > 0}
          <div class="stack" style="--stack:8px;">
            {#each $feeds as f (f.id)}
              <div class="between">
                <div class="row" style="gap:10px;">
                  <Icon name="calendar" size={16} color="var(--fg-3)" />
                  <div>
                    <div style="font-weight:500; font-size:14px;">{f.label}</div>
                    <div class="meta" style="font-size:12px;">
                      {#if f.lastError}
                        <span style="color:var(--status-err);">sync error: {f.lastError}</span>
                      {:else}
                        {syncedLabel(f.lastSyncedAt)}
                      {/if}
                    </div>
                  </div>
                </div>
                <button class="btn-icon" title="Remove feed" onclick={() => removeFeed(f.id)}>
                  <Icon name="archive" size={16} />
                </button>
              </div>
            {/each}
          </div>
          <div class="divider"></div>
        {/if}

        <div class="stack" style="--stack:8px;">
          <label class="eyebrow" for="feed-url" style="display:block;">Add a calendar feed</label>
          <input
            id="feed-url"
            class="input"
            bind:value={feedUrl}
            placeholder="https://calendar.google.com/.../basic.ics"
          />
          <div class="row" style="gap:8px;">
            <input class="input" bind:value={feedLabel} placeholder="Label (optional)" style="flex:1;" />
            <button class="btn btn-accent btn-sm" onclick={submitFeed} disabled={!feedUrl.trim() || feedBusy}>
              <Icon name="plus" size={14} /> {feedBusy ? "Adding…" : "Add feed"}
            </button>
          </div>
          {#if feedError}
            <div class="meta" style="color:var(--status-err);">{feedError}</div>
          {/if}
        </div>
      </div>
    </section>

    <!-- Keyboard shortcuts -->
    <section>
      <div class="display" style="font-size:22px; margin-bottom:4px;">Keyboard shortcuts</div>
      <div class="meta" style="margin-bottom:14px;">EM Notes is keyboard-first.</div>
      <div class="card card-pad">
        <div class="stack" style="--stack:6px;">
          {#each shortcuts as [k, label] (k)}
            <div class="between" style="padding:4px 0;">
              <span style="font-size:14px;">{label}</span>
              <span class="mono"><Kbd>{k}</Kbd></span>
            </div>
          {/each}
        </div>
      </div>
    </section>

    <!-- Integrations (not in local build) -->
    <section>
      <div class="display" style="font-size:22px; margin-bottom:4px;">Integrations & AI</div>
      <div class="meta" style="margin-bottom:14px;">Not part of this local build.</div>
      <div class="card card-pad">
        <div class="row" style="gap:12px;">
          <Icon name="sparkles" size={18} color="var(--fg-3)" />
          <div class="meta" style="flex:1;">
            AI summaries, sentiment analysis, and notifications aren't implemented in the local MVP.
            Your notes stay entirely on this machine.
          </div>
        </div>
      </div>
    </section>
  </div>
</div>
