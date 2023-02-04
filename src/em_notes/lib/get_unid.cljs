(ns em-notes.lib.get-unid
  (:require [em-notes.lib.is-blank-id :refer [is-blank-id]]))

(defn get-unid [keyword map]
  (if (is-blank-id keyword map)
    (str (random-uuid))
    (keyword map)))