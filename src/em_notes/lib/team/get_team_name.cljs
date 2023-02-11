(ns em-notes.lib.team.get-team-name 
  (:require [em-notes.lib.team.get-team-by-id :refer [get-team-by-id]]))

(defn get-team-name [teams id]
  (let [team (get-team-by-id teams id)]
    (:name team)))