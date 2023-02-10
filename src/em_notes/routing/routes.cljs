(ns em-notes.routing.routes
  (:require [em-notes.events :as events]
            [em-notes.views.home :refer [home]]
            [em-notes.views.people.people :refer [people]]
            [em-notes.views.people.person :refer [person]]
            [em-notes.views.all-tasks :refer [tasks]]
            [em-notes.views.teams.team :refer [team]]
            [em-notes.views.teams.teams :refer [teams]]
            [re-frame.core :as re-frame]))

(def routes
  (atom
   {"/"      [home #(identity 1)]
    "/people" [people #(identity 1)]
    "/person" [person (fn [query]
                        (let [event [::events/get-active-person (:id query)]]
                          (re-frame/dispatch [::events/add-to-route-queue [:dispatch event]])))]
    "/teams" [teams #(identity 1)]
    "/team" [team (fn [query]
                    (let [event [::events/set-active-team (:id query)]]
                      (re-frame/dispatch [::events/add-to-route-queue [:dispatch event]])))]
    "/tasks" [tasks #(identity 1)]}))