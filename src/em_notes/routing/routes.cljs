(ns em-notes.routing.routes
  (:require
   [em-notes.views.about :refer [about]]
   [em-notes.views.home :refer [home]]
   [em-notes.views.note :refer [create-note]]
   [em-notes.views.people.person :refer [create-person]]
   [em-notes.views.people.people :refer [people]]))

(def routes
  (atom
   {"/"      [home]
    "/about" [about]
    "/note" [create-note]
    "/people" [people]
    "/create-person" [create-person]}))