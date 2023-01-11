(ns em-notes.routing.routes
  (:require
   [em-notes.views.about :refer [about]]
   [em-notes.views.home :refer [home]]
   [em-notes.views.note :refer [create-note]]))

(def routes
  (atom
   {"/"      [home]
    "/about" [about]
    "/note" [create-note]}))