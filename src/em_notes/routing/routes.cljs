(ns em-notes.routing.routes
  (:require
   [re-frame.core :as re-frame]
   [em-notes.events :as events]
   [em-notes.views.home :refer [home]]
   [em-notes.views.people.person :refer [person]]
   [em-notes.views.people.people :refer [people]]))

(def routes
  (atom
   {"/"      [home #(identity 1)]
    "/people" [people #(identity 1)]
    "/person" [person (fn [query]
                        (let [event [::events/set-active-person (:id query)]]
                          (re-frame/dispatch [::events/add-to-route-queue [:dispatch event]])))]}))