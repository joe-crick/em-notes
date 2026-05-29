// Curated re-exports of the `ljsp-core` collection/data helpers used across the app's
// transforms. Keeping them behind @em-notes/core means app packages depend on the local
// helper package (per the plan's dependency graph §4.4) rather than importing ljsp-core
// directly, and gives us one place to wrap or swap helpers later.
export {
  assoc,
  map,
  filter,
  reduce,
  groupBy,
  get,
  rename,
  entries,
  tf,
  count,
  partial,
  lte$,
} from "ljsp-core";
