(ns em-notes.routing.routes
  (:require
   [em-notes.views.about :refer [about-panel]]
   [em-notes.views.home :refer [home-panel]]
   [em-notes.views.note :refer [note-panel]]))

(def routes
  (atom
   {"/"      [home-panel]
    "/about" [about-panel]
    "/note" [note-panel]}))