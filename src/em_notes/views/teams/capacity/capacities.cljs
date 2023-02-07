(ns em-notes.views.teams.capacity.capacities
  (:require [em-notes.components.left-right-cols :refer [left-right]]
            [em-notes.events :as events]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.bulma-cls :refer [bulma-cls]]
            [em-notes.lib.show-confirm :refer [show-confirm]]
            [em-notes.lib.show-modal :refer [show-modal]]
            [em-notes.lib.table-style :refer [table-style]]
            [em-notes.subs :as subs]
            [em-notes.views.teams.capacity.capacity :refer [capacity]]
            [re-frame.core :as rf]))

(defn capacities []
  (let [active-team (rf/subscribe [::subs/active-team])
        capacity-view capacity] 
    (fn []
      [:div.container
       [left-right (fn [] [:h1 {:class (bulma-cls :subtitle)}
                           (grab :capacities/title)])
        (fn [] [:button {:class "button is-primary"
                         :on-click #(show-modal (grab :capacity/title) capacity-view)} (grab :capacities/create-capacity)])]
       [:table {:class (table-style)}
        [:thead
         [:tr
          [:th (grab :capacity/week-of)]
          [:th (grab :capacity/perf)]
          [:th (grab :capacity/alignment)]
          [:th (grab :capacity/development)]
          [:th (grab :capacity/next-steps)]
          [:th (grab :capacity/notes)]
          [:th (grab :table/actions)]]]
        [:tbody
         (for [capacity (get-in @active-team [:data :capacities])
               :let [[_ capacity] capacity
                     capacity-id (:capacity-id capacity)]]
           ^{:key (random-uuid)} [:tr {:id capacity-id}
                                  [:td.name
                                   [:button {:class "button is-ghost"
                                             :on-click #(rf/dispatch [::events/edit-capacity [@active-team capacity capacity-view]])} (:week-of capacity)]]
                                  [:td {:class "pt-4"} (:perf capacity)]
                                  [:td {:class "pt-4"} (:alignment capacity)]
                                  [:td {:class "pt-4"} (:development capacity)]
                                  [:td {:class "pt-4"} (:next-steps capacity)]
                                  [:td {:class "pt-4"} (:notes capacity)]
                                  [:td
                                   [:div {:class "buttons are-small is-grouped"}
                                    [:button {:class "button is-danger is-fixed-50"
                                              :on-click  #(show-confirm (grab :capacities/confirm-delete) [::events/delete-item [@active-team capacity :capacities :capacity-id]])} (grab :form/delete)]]]])]]])))