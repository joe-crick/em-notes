(ns em-notes.components.fields.text-input 
  (:require [em-notes.components.fields.input :refer [input]]))

(defn text-input [atom revise! config]
  (input atom revise! config "text"))

(defn set-text-input [atom revise!]
  (partial text-input atom revise!))