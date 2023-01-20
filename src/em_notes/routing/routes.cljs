(ns em-notes.routing.routes
  (:require
   [em-notes.views.about :refer [about]]
   [em-notes.views.home :refer [home]]
   [em-notes.views.note :refer [create-note]]
   [em-notes.views.person :refer [create-person]]))

(def routes
  (atom
   {"/"      [home]
    "/about" [about]
    "/note" [create-note]
    "/person" [create-person]}))