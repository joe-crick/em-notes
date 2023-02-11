(ns em-notes.views.teams.capacity.capacities
  (:require [em-notes.components.left-right-cols :refer [left-right]]
            [em-notes.components.table-filter :refer [table-filter]]
            [em-notes.events :as events]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.css-cls :refer [css-cls]]
            [em-notes.lib.filter-map-on-prop :refer [filter-map-on-prop]]
            [em-notes.lib.local-state :refer [local-state]]
            [em-notes.lib.show-confirm :refer [show-confirm]]
            [em-notes.lib.show-modal :refer [show-modal]]
            [em-notes.lib.table-style :refer [table-style]]
            [em-notes.subs :as subs]
            [em-notes.views.teams.capacity.capacity :refer [capacity]]
            [re-frame.core :as rf]))

(defn capacities []
  (let [active-team (rf/subscribe [::subs/active-team])
        capacity-view capacity
        [filter revise!] (local-state {:filter ""})] 
    (fn []
      [:div.container
       [left-right (fn [] [:h1 {:class (css-cls :subtitle)}
                           (grab :capacities/title)])
        (fn [] [:button {:class (css-cls :button :is-primary)
                         :on-click #(show-modal (grab :capacity/title) capacity-view)} (grab :capacities/create-capacity)])]
       [table-filter filter revise!]
       [:table {:class (table-style)}
        [:thead
         [:tr
          [:th (grab :capacity/week)]
          [:th (grab :capacity/percent-capacity)]
          [:th (grab :capacity/notes)]
          [:th (grab :table/actions)]]]
        [:tbody
         (for [capacity (filter-map-on-prop (or (get-in @active-team [:data :capacities]) []) [:week-of] (:filter @filter))
               :let [capacity-id (:capacity-id capacity)]]
           ^{:key (random-uuid)} [:tr {:id capacity-id}
                                  [:td.name
                                   [:button {:class (css-cls :button :is-ghost)
                                             :on-click #(rf/dispatch [::events/edit-capacity [@active-team capacity capacity-view]])} (:week-of capacity)]]
                                  [:td {:class (css-cls :pt-4)} (:percent-capacity capacity)]
                                  [:td {:class (css-cls :pt-4)} (:notes capacity)]
                                  [:td
                                   [:div {:class (css-cls :buttons :are-small :is-grouped)}
                                    [:button {:class (css-cls :button :is-danger :is-fixed-50)
                                              :on-click  #(show-confirm (grab :capacities/confirm-delete) [::events/delete-capacity [@active-team capacity]])} (grab :form/delete)]]]])]]])))