(ns em-notes.views.people.person
  (:require [em-notes.components.card :refer [card]]
            [em-notes.components.left-right-cols :refer [left-right]]
            [em-notes.events :as events]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.local-state :refer [local-state]]
            [em-notes.lib.show-confirm :refer [show-confirm]]
            [em-notes.routing.nav :as nav]
            [em-notes.subs :as subs]
            [em-notes.views.people.person-profile :refer [person-profile]]
            [em-notes.views.tasks.tasks :refer [tasks]]
            [re-frame.core :as rf]))

(defn task-view [active-person]
  [tasks active-person])

(defn performance []
  [:div.container "A record of a person's performance over the last reporting period. Contains productivity and soft skills notes"])

(defn profile [active-person]
  [person-profile active-person])

(defn career-growth []
  [:div.container "Need to be able to load in a career ladder, then track a person's progress against that ladder"])

(defn active-tab [tab active-person]
  (case tab
    :tasks [task-view active-person]
    :performance [performance]
    :career-growth [career-growth]
    [profile active-person]))

(defn current-tab? [tab cur-tab]
  (if (= cur-tab tab) "is-info" ""))

(defn person []
  (let [active-person (rf/subscribe [::subs/active-person])
        [tab change-tab!] (local-state :profile)]
    (fn []
      [:section {:style {:margin-top "-40px"}}
       [:div {:class "container"}
        [:button {:class "button is-ghost mt-5" :on-click #(nav/go :home)} (str "< " (grab :home/home))]]
       [:div {:class "container mb-1"}
        [left-right (fn []) (fn [] [:div
                                    [:button {:class (str "button " (current-tab? @tab :profile)) :on-click #(change-tab! :profile)} (grab :person/profile)]
                                    [:button {:class (str "button " (current-tab? @tab :performance)) :on-click #(change-tab! :performance)} (grab :person/performance)]
                                    [:button {:class (str "button " (current-tab? @tab :career-growth)) :on-click #(change-tab! :career-growth)} (grab :person/career-growth)]
                                    [:button {:class (str "button " (current-tab? @tab :tasks)) :on-click #(change-tab! :tasks)} (grab :person/tasks)]])]]
       [:div.container
        [left-right (fn [])
         (fn [] [:div.container
                 [:button {:class "button is-danger mt-5 mb-3"
                           :on-click #(show-confirm (grab :person/confirm-delete) [::events/delete-person @active-person])} (str (grab :form/delete) " " (grab :person/title))]])]]

       ;; reagent makes us ref this rendered in the view, or the reactivity won't work
       [:div.is-hidden (:first-name @active-person)]
       [card
        (fn [] [:div
                [:h1 {:class "title"}
                 (str (:first-name @active-person) " " (:last-name @active-person))]
                [active-tab @tab active-person]])]])))