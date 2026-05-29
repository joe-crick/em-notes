<script>
  import { onMount } from "svelte";
  import { session, refreshSession } from "./lib/stores/session.js";
  import { route } from "./lib/stores/route.js";
  import { loadPeople } from "./lib/stores/people.js";
  import { loadActions } from "./lib/stores/actions.js";
  import { loadSettings } from "./lib/stores/settings.js";
  import {
    newNotePerson,
    addReportOpen,
    closeNewNote,
    closeAddReport,
  } from "./lib/stores/ui.js";
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
    }
  });

  // Escape closes whichever overlay is open.
  function onKeydown(e) {
    if (e.key !== "Escape") return;
    if ($newNotePerson) closeNewNote();
    else if ($addReportOpen) closeAddReport();
  }

  const screens = { home: Home, team: Team, person: Person, actions: Actions, settings: Settings };
  const Screen = $derived(screens[$route.name] ?? Home);
</script>

<svelte:window onkeydown={onKeydown} />

{#if !booted || $session.status === "loading"}
  <div style="height:100vh; display:grid; place-items:center; color:var(--fg-3);">Loading…</div>
{:else if !$session.authenticated}
  <Login />
{:else}
  <div class="app">
    <Topbar />
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
{/if}
