(ns em-notes.lib.join-str
  (:require [clojure.string :refer (join)]))

(defn join-str [vec, sep]
  (join sep vec))