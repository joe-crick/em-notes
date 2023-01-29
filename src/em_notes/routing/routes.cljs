(ns em-notes.routing.routes
  (:require
   [re-frame.core :as re-frame]
   [em-notes.events :as events]
   [em-notes.views.about :refer [about]]
   [em-notes.views.home :refer [home]]
   [em-notes.views.note :refer [create-note]]
   [em-notes.views.people.person :refer [person]]
   [em-notes.views.people.people :refer [people]]
   [em-notes.views.tasks.tasks :refer [tasks]]
   [em-notes.views.tasks.task :refer [task]]))

(def routes
  (atom
   {"/"      [home #(identity 1)]
    "/about" [about #(identity 1)]
    "/note" [create-note #(identity 1)]
    "/people" [people #(identity 1)]
    "/person" [person (fn [query] 
                        (let [event [::events/set-active-person (:id query)]]
                          (re-frame/dispatch [::events/add-to-route-queue [event [:dispatch event]]])))]
    "/tasks" [tasks (fn [query]
                      (re-frame/dispatch [::events/add-to-route-queue [:dispatch [::events/set-active-task (:id query)]]]))]
    "/task" [task #(identity 1)]}))