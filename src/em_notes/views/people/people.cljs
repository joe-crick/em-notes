(ns em-notes.views.people.people
    (:require 
     [em-notes.lib.get-state :refer [get-state]] 
     [em-notes.routing.nav :as nav]
     [em-notes.i18n.tr :refer [grab]]))

(defn people []
  ;; setup local state
  (let [[tab revise!] (get-state :overview)]
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
          [:button {:class "button is-ghost" :on-click #(revise! :tasks)} (grab :person/tasks)]]

         [:form
          [:div.field
           [:p.control
            [active-tab @tab]]]]]]])))

(defn tasks []
  [:div "Tasks"])

(defn performance []
  [:div "Performance"])

(defn overview []
  [:div "Overview"])

(defn active-tab [tab]
  (case tab
    :tasks [tasks]
    :performance [performance]
    [overview]))