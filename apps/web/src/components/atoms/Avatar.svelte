<script>
  let { person, size = "md" } = $props();
</script>

{#if person}
  <span class="avatar avatar-{size}" style="background:{person.color}; color:white;">
    {person.initials}
  </span>
{/if}
