(ns em-notes.lib.get-state
  (:require
   [reagent.core :as r]
   [em-notes.lib.update-atom :refer [set-revise]]))

(defn get-state [val]
  (let [atom (r/atom val)
        revise! (set-revise atom)]
    [atom revise!]))