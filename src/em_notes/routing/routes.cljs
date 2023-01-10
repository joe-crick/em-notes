(ns em-notes.routing.routes
  (:require
   [em-notes.views.about :as about]
   [em-notes.views.home :as home]))

(def routes
  (atom
   {"/"      [home/home-panel]
    "/about" [about/about-panel]}))