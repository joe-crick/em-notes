<script>
  import { settings, updateSettings } from "../lib/stores/settings.js";
  import { ME } from "../lib/manager.js";
  import Avatar from "../components/atoms/Avatar.svelte";
  import Icon from "../components/atoms/Icon.svelte";
  import Kbd from "../components/atoms/Kbd.svelte";

  const themes = [
    ["light", "Light", "sun"],
    ["dark", "Dark", "moon"],
  ];
  const densities = [
    ["comfortable", "Comfortable"],
    ["compact", "Compact"],
  ];

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
            <div class="meta">{ME.role} · {ME.team}</div>
          </div>
        </div>
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
            Calendar sync, AI summaries, sentiment analysis, and notifications aren't implemented in
            the local MVP. Your notes stay entirely on this machine.
          </div>
        </div>
      </div>
    </section>
  </div>
</div>
