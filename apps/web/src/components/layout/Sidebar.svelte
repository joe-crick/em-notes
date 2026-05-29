<script>
  import { route, goTo } from "../../lib/stores/route.js";
  import { people } from "../../lib/stores/people.js";
  import { logout } from "../../lib/stores/session.js";
  import NavItem from "./NavItem.svelte";

  function reportSub(p) {
    if (p.flags?.includes("sentiment-drop")) return "↓ sentiment";
    if (p.flags?.includes("promotion-ready")) return "promo-ready";
    return null;
  }
</script>

<aside class="app-sidebar" style="display:flex; flex-direction:column;">
  <div class="stack" style="--stack:2px;">
    <NavItem
      icon="home"
      label="Home"
      active={$route.name === "home"}
      onClick={() => goTo("home")}
      shortcut="G H"
    />
    <NavItem
      icon="team"
      label="Team"
      active={$route.name === "team"}
      onClick={() => goTo("team")}
      shortcut="G T"
      badge={$people.length}
    />
    <NavItem
      icon="actions"
      label="Actions"
      active={$route.name === "actions"}
      onClick={() => goTo("actions")}
    />
    <NavItem icon="calendar" label="Calendar" onClick={() => goTo("home")} />
  </div>

  <div class="nav-section">Direct reports</div>
  <div class="stack" style="--stack:2px;">
    {#each $people as p, i (p.id)}
      <NavItem
        avatar={p}
        label={p.name}
        active={$route.name === "person" && $route.personId === p.id}
        sub={reportSub(p)}
        badge={p.openActions > 0 ? p.openActions : null}
        onClick={() => goTo("person", p.id)}
        shortcut={i < 9 ? `G ${i + 1}` : null}
      />
    {/each}
  </div>

  <div style="flex:1"></div>
  <div class="stack" style="--stack:2px;">
    <NavItem
      icon="settings"
      label="Settings"
      active={$route.name === "settings"}
      onClick={() => goTo("settings")}
      shortcut="G S"
    />
    <NavItem icon="arrow" label="Sign out" onClick={logout} />
  </div>
</aside>
