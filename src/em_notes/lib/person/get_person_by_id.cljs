(ns em-notes.lib.person.get-person-by-id)

(defn get-person-by-id [people id]
  (let [people-list (vals people)]
    (first (filter #(= (:person_id %) id) people-list))))