(ns em-notes.lib.get-state
  (:require
   [reagent.core :as r]
   [em-notes.lib.update-atom :refer [set-revise]]))

(defn get-state [val]
  (let [data :overview
        atom (r/atom data)
        revise! (set-revise atom)]
    [atom revise!]))