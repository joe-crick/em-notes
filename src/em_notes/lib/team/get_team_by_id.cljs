(ns em-notes.lib.team.get-team-by-id)

(defn get-team-by-id [teams id]
  (let [team-list (vals teams)]
    (first (filter #(= (:team-id %) id) team-list))))