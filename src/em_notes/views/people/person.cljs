(ns em-notes.views.people.person
  (:require [em-notes.lib.local-state :refer [local-state]]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.views.people.person-overview :refer [person-overivew]]))

(defn tasks []
  [:div "Open and completed tasks related to this person"])

(defn performance []
  [:div "A record of a person's performance over the last reporting period. Contains productivity and soft skills notes"])

(defn overview []
  [person-overivew])

(defn career-growth []
  [:div "Need to be able to load in a career ladder, then track a person's progress against that ladder"])

(defn active-tab [tab]
  (case tab
    :tasks [tasks]
    :performance [performance]
    :career-growth [career-growth]
    [overview]))

(defn person []
  (let [[tab change-tab!] (local-state :overview)]
    (fn []
      [:section
       [:div.container
        [:button {:class "button is-ghost" :on-click #(change-tab! :overview)} (grab :person/overview)]
        [:button {:class "button is-ghost" :on-click #(change-tab! :performance)} (grab :person/performance)]
        [:button {:class "button is-ghost" :on-click #(change-tab! :career-growth)} (grab :person/career-growth)]
        [:button {:class "button is-ghost" :on-click #(change-tab! :tasks)} (grab :person/tasks)]]
       [:div
        [:p
         [active-tab @tab]]]])))