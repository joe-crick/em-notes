(ns em-notes.lib.bulma-cls
  (:require [clojure.string :refer (join)]))

(defn bulma-cls [key & keys]
  (let [base-class [key]
        classes (if-not (nil? keys) (apply conj base-class keys) base-class)]
    (join " " (map #(name %) classes))))