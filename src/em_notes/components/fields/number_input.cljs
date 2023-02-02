(ns em-notes.components.fields.number-input 
  (:require [em-notes.components.fields.input :refer [input]]))

(defn number-input [atom revise! config]
  (input atom revise! config "number"))

(defn set-number-input [atom revise!]
  (partial number-input atom revise!))