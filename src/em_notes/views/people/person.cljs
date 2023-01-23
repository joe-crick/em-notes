(ns em-notes.views.people.person
  (:require [em-notes.lib.local-state :refer [local-state]]
            [em-notes.i18n.tr :refer [grab]]
            [re-frame.core :as rf]
            [em-notes.views.people.person-overview :refer [person-overivew]]
            [em-notes.subs :as subs]
            [em-notes.views.tasks.tasks :refer [tasks]]))

(defn task-view [person revise!]
  [tasks person revise!])

(defn performance []
  [:div "A record of a person's performance over the last reporting period. Contains productivity and soft skills notes"])

(defn overview [person revise!]
  [person-overivew person revise!])

(defn career-growth []
  [:div "Need to be able to load in a career ladder, then track a person's progress against that ladder"])

(defn active-tab [tab person revise!]
  (case tab
    :tasks [task-view person revise!]
    :performance [performance]
    :career-growth [career-growth]
    [overview person revise!]))

(defn person []
  (let [active-person (rf/subscribe [::subs/active-person])
    [person revise!] (local-state @active-person)
    [tab change-tab!] (local-state :overview)]
    (fn []
      [:section
       [:div.container
        [:button {:class "button is-ghost" :on-click #(change-tab! :overview)} (grab :person/overview)]
        [:button {:class "button is-ghost" :on-click #(change-tab! :performance)} (grab :person/performance)]
        [:button {:class "button is-ghost" :on-click #(change-tab! :career-growth)} (grab :person/career-growth)]
        [:button {:class "button is-ghost" :on-click #(change-tab! :tasks)} (grab :person/tasks)]]
       [:div
         [active-tab @tab person revise!]]])))