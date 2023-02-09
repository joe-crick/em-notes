(ns em-notes.lib.get-unid
  (:require [em-notes.lib.is-blank :refer [is-blank?]]))

(defn get-unid [keyword map]
  (if (is-blank? keyword map)
    (str (random-uuid))
    (keyword map)))