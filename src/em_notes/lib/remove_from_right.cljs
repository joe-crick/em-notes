(ns em-notes.lib.remove-from-right
  (:require [clojure.set :refer [difference]]))

(defn remove-from-right [left right]
  (difference (set left) (set right)))