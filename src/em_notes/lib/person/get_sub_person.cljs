(ns em-notes.lib.person.get-sub-person)

(defn get-sub-person [person]
  {:first-name (:first-name person)
   :last-name (:last-name person)
   :team (:team person)
   :person-id (:person-id person)})