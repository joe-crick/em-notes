(ns em-notes.lib.get-unid
  (:require [em-notes.lib.is-blank-id :refer [is-blank-id]]))

(defn get-unid [keyword map] 
  (let [new? (is-blank-id :keyword map)]
    (if new? (str (random-uuid)) (keyword map))))