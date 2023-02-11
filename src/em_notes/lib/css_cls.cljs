(ns em-notes.lib.css-cls
  (:require [clojure.string :refer (join)]))

(defn css-cls [key & keys]
  (let [base-class [key]
        classes (if-not (nil? keys) (apply conj base-class keys) base-class)]
    (join " " (map #(name %) classes))))