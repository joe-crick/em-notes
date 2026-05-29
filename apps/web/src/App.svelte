<script>
  import { onMount } from "svelte";
  import { session, refreshSession } from "./lib/stores/session.js";
  import { route } from "./lib/stores/route.js";
  import { loadPeople } from "./lib/stores/people.js";
  import { loadActions } from "./lib/stores/actions.js";
  import { loadSettings } from "./lib/stores/settings.js";
  import { loadFeeds, loadAgenda } from "./lib/stores/calendar.js";
  import {
    newNotePerson,
    addReportOpen,
    editPerson,
    deletePersonTarget,
    paletteOpen,
    openPalette,
  } from "./lib/stores/ui.js";
  import { handleKeydown } from "./lib/keyboard/shortcuts.js";
  import Login from "./routes/Login.svelte";
  import Topbar from "./components/layout/Topbar.svelte";
  import Sidebar from "./components/layout/Sidebar.svelte";
  import Home from "./routes/Home.svelte";
  import Team from "./routes/Team.svelte";
  import Person from "./routes/Person.svelte";
  import Actions from "./routes/Actions.svelte";
  import Settings from "./routes/Settings.svelte";
  import NewNoteModal from "./components/notes/NewNoteModal.svelte";
  import AddReportModal from "./components/team/AddReportModal.svelte";
  import EditPersonModal from "./components/team/EditPersonModal.svelte";
  import DeletePersonModal from "./components/team/DeletePersonModal.svelte";
  import CommandPalette from "./components/layout/CommandPalette.svelte";

  let booted = $state(false);

  onMount(async () => {
    await refreshSession();
    booted = true;
  });

  // Load all app data once authenticated.
  $effect(() => {
    if ($session.authenticated) {
      loadPeople();
      loadActions();
      loadSettings();
      loadFeeds();
      loadAgenda();
    }
  });

  const screens = { home: Home, team: Team, person: Person, actions: Actions, settings: Settings };
  const Screen = $derived(screens[$route.name] ?? Home);
</script>

<svelte:window onkeydown={handleKeydown} />

{#if !booted || $session.status === "loading"}
  <div style="height:100vh; display:grid; place-items:center; color:var(--fg-3);">Loading…</div>
{:else if !$session.authenticated}
  <Login />
{:else}
  <div class="app">
    <Topbar onOpenPalette={openPalette} />
    <Sidebar />
    <main class="app-main">
      <Screen />
    </main>
  </div>

  {#if $newNotePerson}
    <NewNoteModal person={$newNotePerson} />
  {/if}
  {#if $addReportOpen}
    <AddReportModal />
  {/if}
  {#if $editPerson}
    <EditPersonModal person={$editPerson} />
  {/if}
  {#if $deletePersonTarget}
    <DeletePersonModal person={$deletePersonTarget} />
  {/if}
  {#if $paletteOpen}
    <CommandPalette />
  {/if}
{/if}
