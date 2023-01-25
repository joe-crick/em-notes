(ns em-notes.components.fields.date-input
  (:require [em-notes.components.fields.input :refer [input]]))

(defn date-input [atom revise! config]
  (input atom revise! config "date"))

(defn set-date-input [atom revise!]
  (partial date-input atom revise!))