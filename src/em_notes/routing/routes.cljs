(ns em-notes.routing.routes
  (:require
   [em-notes.views.about :refer [about]]
   [em-notes.views.home :refer [home]]
   [em-notes.views.note :refer [create-note]]
   [em-notes.views.people.person :refer [person]]
   [em-notes.views.people.people :refer [people]]
   [em-notes.views.tasks.tasks :refer [tasks]]
   [em-notes.views.tasks.task :refer [task]]))

(def routes
  (atom
   {"/"      [home]
    "/about" [about]
    "/note" [create-note]
    "/people" [people]
    "/person" [person]
    "/tasks" [tasks]
    "/task" [task]}))