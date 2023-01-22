(ns em-notes.views.people.people
    (:require 
     [em-notes.lib.local-state :refer [local-state]] 
     [em-notes.routing.nav :as nav]
     [em-notes.i18n.tr :refer [grab]]))

(defn tasks []
  [:div "Open and completed tasks related to this person"])

(defn performance []
  [:div "A record of a person's performance over the last reporting period. Contains productivity and soft skills notes"])

(defn overview []
  [:div "Profile of person"])

(defn career-growth []
  [:div "Need to be able to load in a career ladder, then track a person's progress against that ladder"])

(defn active-tab [tab]
  (case tab
    :tasks [tasks]
    :performance [performance]
    :career-growth [career-growth]
    [overview]))



(defn people []
  ;; setup local state
  (let [[tab revise!] (local-state :overview)]
    ;; required when local state is used, because we need to return a render function
    (fn []
      [:section
       [:div.container
        [:div
         [:h1 {:class "title mt-5"}
          (grab :person/title)]

         [:div.container
          [:div>button {:class "button is-link" :on-click #(nav/go :create-person)}
           (grab :home/create-person)]]

         [:div.container
          [:button {:class "button is-ghost" :on-click #(revise! :overview)} (grab :person/overview)]
          [:button {:class "button is-ghost" :on-click #(revise! :performance)} (grab :person/performance)]
          [:button {:class "button is-ghost" :on-click #(revise! :career-growth)} (grab :person/career-growth)]
          [:button {:class "button is-ghost" :on-click #(revise! :tasks)} (grab :person/tasks)]]

         [:form
          [:div.field
           [:p.control
            [active-tab @tab]]]]]]])))

