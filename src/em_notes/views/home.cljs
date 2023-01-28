(ns em-notes.views.home
  (:require
   [em-notes.views.people.people :refer [people]]))

(defn home []
  [people])