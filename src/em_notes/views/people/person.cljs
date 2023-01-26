(ns em-notes.views.people.person
  (:require [em-notes.lib.local-state :refer [local-state]]
            [em-notes.i18n.tr :refer [grab]]
            [re-frame.core :as rf]
            [em-notes.views.people.person-overview :refer [person-overivew]]
            [em-notes.subs :as subs]
            [em-notes.events :as events]
            [em-notes.views.tasks.tasks :refer [tasks]]
            [em-notes.routing.nav :as nav]))

(defn task-view [active-person]
  [tasks (:tasks @active-person)])

(defn performance []
  [:div "A record of a person's performance over the last reporting period. Contains productivity and soft skills notes"])

(defn overview [active-person]
  [person-overivew @active-person])

(defn career-growth []
  [:div "Need to be able to load in a career ladder, then track a person's progress against that ladder"])

(defn active-tab [tab active-person]
  (case tab
    :tasks [task-view active-person]
    :performance [performance]
    :career-growth [career-growth]
    [overview active-person]))

(defn person [person-id]
  (let [active-person (rf/subscribe [::subs/active-person])
        [tab change-tab!] (local-state :overview)] 
    (println "person-id: " (:id person-id))
    (fn []
      [:section
       [:div.container
        [:button {:class "button is-ghost" :on-click #(change-tab! :overview)} (grab :person/overview)]
        [:button {:class "button is-ghost" :on-click #(change-tab! :performance)} (grab :person/performance)]
        [:button {:class "button is-ghost" :on-click #(change-tab! :career-growth)} (grab :person/career-growth)]
        [:button {:class "button is-ghost" :on-click #(change-tab! :tasks)} (grab :person/tasks)]]
       [:div {:class "container is-flex is-justify-content-space-between"}
        [:button {:class "button is-info mt-5" :on-click #(nav/go :home)} (str "< " (grab :home/home))]
        [:button {:class "button is-danger mt-5"
                  :on-click #(rf/dispatch-sync [::events/delete-person @active-person])} (grab :form/delete)]]

       [:div
        [active-tab @tab active-person]]])))