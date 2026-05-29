<script>
  import { deletePerson } from "../../lib/stores/people.js";
  import { closeDeletePerson } from "../../lib/stores/ui.js";
  import { goTo } from "../../lib/stores/route.js";
  import Avatar from "../atoms/Avatar.svelte";
  import Icon from "../atoms/Icon.svelte";

  // Confirm + perform a destructive delete (DELETE /api/people/:id). Cascades notes/sentiment and
  // unlinks actions, so it's an explicit confirm step (review follow-up #9).
  let { person } = $props();

  let busy = $state(false);
  let error = $state("");

  async function confirm() {
    if (busy) return;
    busy = true;
    error = "";
    const res = await deletePerson(person.id);
    busy = false;
    if (res.ok) {
      closeDeletePerson();
      goTo("team");
    } else {
      error = res.error?.message ?? "Could not delete this report.";
    }
  }
</script>

<div
  class="modal-scrim"
  onclick={(e) => e.target === e.currentTarget && closeDeletePerson()}
  role="presentation"
>
  <div class="modal" style="width:460px;" role="dialog" aria-modal="true" tabindex="-1">
    <div style="padding:22px;">
      <div class="row" style="gap:14px; margin-bottom:14px;">
        <Avatar {person} size="md" />
        <div>
          <div class="eyebrow" style="color:var(--status-err);">Delete report</div>
          <div class="display" style="font-size:22px;">Delete {person.name}?</div>
        </div>
      </div>
      <div class="meta" style="line-height:1.55;">
        This permanently removes {person.name} plus their 1:1 notes and sentiment history, and
        unlinks their action items. This can't be undone.
      </div>
      {#if error}
        <div class="meta" style="color:var(--status-err); margin-top:12px;">{error}</div>
      {/if}
    </div>

    <div class="between" style="padding:14px 22px; border-top:1px solid var(--line); background:var(--bg-surface);">
      <button class="btn btn-ghost btn-sm" onclick={closeDeletePerson} disabled={busy}>Cancel</button>
      <button
        class="btn btn-sm"
        style="background:var(--status-err); color:#fff;"
        onclick={confirm}
        disabled={busy}
      >
        <Icon name="archive" size={14} />
        {busy ? "Deleting…" : "Delete permanently"}
      </button>
    </div>
  </div>
</div>
