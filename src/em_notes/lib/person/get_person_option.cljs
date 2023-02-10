(ns em-notes.lib.person.get-person-option 
  (:require [em-notes.lib.person.get-person-id :refer [get-person-id]]
            [em-notes.lib.person.person-full-name :refer [person-full-name]]))

(defn get-person-option [person]
  {:value (get-person-id person) :label (person-full-name person)})