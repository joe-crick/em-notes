(ns em-notes.lib.person.get-person-by-id)

(defn get-person-by-id [people id]
  (prn "people" people)
  (prn "id" id)
  (first (filter #(= (:person_id %) id) people)))