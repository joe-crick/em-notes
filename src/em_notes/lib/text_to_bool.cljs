(ns em-notes.lib.text-to-bool
  (:require [em-notes.lib.lower-case :refer [lower-case]]))

(defn text-to-bool [text] 
  (if (= (lower-case text) "true") true false))