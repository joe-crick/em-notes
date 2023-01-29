(ns em-notes.lib.get-person-id
  (:require  
   [em-notes.lib.lower-case :refer [lower-case]]))

(defn get-person-id [person]
  (lower-case (str (:first-name person) "-" (:last-name person)))
  )