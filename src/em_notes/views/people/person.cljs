(ns em-notes.views.people.person
  (:require [em-notes.lib.local-state :refer [local-state]]
            [em-notes.i18n.tr :refer [grab]]
            [re-frame.core :as rf]
            [em-notes.views.people.person-overview :refer [person-overivew]]
            [em-notes.subs :as subs]
            [em-notes.events :as events]
            [em-notes.views.tasks.tasks :refer [tasks]]
            [em-notes.components.left-right-cols :refer [left-right]]
            [em-notes.components.card :refer [card]]
            [em-notes.routing.nav :as nav]))

(defn task-view [active-person]
  [tasks (:tasks @active-person)])

(defn performance []
  [:div.container "A record of a person's performance over the last reporting period. Contains productivity and soft skills notes"])

(defn overview [active-person]
  [person-overivew @active-person])

(defn career-growth []
  [:div.container "Need to be able to load in a career ladder, then track a person's progress against that ladder"])

(defn active-tab [tab active-person]
  (case tab
    :tasks [task-view active-person]
    :performance [performance]
    :career-growth [career-growth]
    [overview active-person]))

(defn current-tab? [tab cur-tab]
  (if (= cur-tab tab) "is-info" ""))

(defn person []
  (let [active-person (rf/subscribe [::subs/active-person])
        [tab change-tab!] (local-state :overview)]
    (fn []
      [:section {:style {:margin-top "-40px"}}
       [:div {:class "container"}
        [:button {:class "button is-ghost mt-5" :on-click #(nav/go :home)} (str "< " (grab :home/home))]]
       [:div {:class "container mb-1"}
        [left-right (fn []) (fn [] [:div
                                    [:button {:class (str "button " (current-tab? @tab :overview)) :on-click #(change-tab! :overview)} (grab :person/overview)]
                                    [:button {:class (str "button " (current-tab? @tab :performance)) :on-click #(change-tab! :performance)} (grab :person/performance)]
                                    [:button {:class (str "button " (current-tab? @tab :career-growth)) :on-click #(change-tab! :career-growth)} (grab :person/career-growth)]
                                    [:button {:class (str "button " (current-tab? @tab :tasks)) :on-click #(change-tab! :tasks)} (grab :person/tasks)]])]]
       [:div.container
        [left-right (fn [])
         (fn [] [:div.container
                 [:button {:class "button is-danger mt-5 mb-3"
                           :on-click #(rf/dispatch [::events/show-confirm {:msg (grab :person/confirm-delete)
                                                                          :on-confirm [::events/delete-person @active-person]
                                                                          :display "is-block"}])} (str (grab :form/delete) " " (grab :person/title))]])]]

       [card
        (fn [] [active-tab @tab active-person])]])))