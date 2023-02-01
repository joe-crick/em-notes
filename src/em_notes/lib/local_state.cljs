(ns em-notes.lib.local-state
  (:require
   [reagent.core :as r]
   [em-notes.lib.revise :refer [set-revise]]))

(defn local-state [val]
  "Given any valid data structure, will create an atom for it, and a revise! function,
   and return them as a tuple in a Vector."
  (let [atom (r/atom val)
        revise! (set-revise atom)]
    [atom revise!]))