(ns em-notes.views.people.one-on-ones.one-on-ones
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
            [em-notes.views.people.one-on-ones.one-on-one :refer [one_on_one]]
            [re-frame.core :as rf]))

(defn one-on-ones []
  (let [active-person (rf/subscribe [::subs/active-person])
        one-on-one-view one_on_one
        [filter revise!] (local-state {:filter ""})] 
    (fn []
      [:div.container
       [left-right (fn [] [:h1 {:class (css-cls :subtitle)}
                           (grab :one-on-ones/title)])
        (fn [] [:button {:class (css-cls :button :is-primary)
                         :on-click #(show-modal (grab :one-on-one/title) one_on_one)} (grab :one-on-ones/create-one-on-one)])]
       [table-filter filter revise!]
       [:table {:class (table-style)}
        [:thead
         [:tr
          [:th (grab :one-on-one/week-of)]
          [:th (grab :one-on-one/perf)]
          [:th (grab :one-on-one/alignment)]
          [:th (grab :one-on-one/development)]
          [:th (grab :one-on-one/next-steps)]
          [:th (grab :one-on-one/notes)]
          [:th (grab :table/actions)]]]
        [:tbody
         (for [one-on-one (filter-map-on-prop (or (get-in @active-person [:data :one-on-ones]) []) [:week-of] (:filter @filter))
               :let [one-on-one-id (:one-on-one-id one-on-one)]]
           ^{:key (random-uuid)} [:tr {:id one-on-one-id}
                                  [:td.name
                                   [:button {:class (css-cls :button :is-ghost)
                                             :on-click #(rf/dispatch [::events/edit-one-on-one [@active-person one-on-one one-on-one-view]])} (:week-of one-on-one)]]
                                  [:td {:class (css-cls :pt-4)} (:perf one-on-one)]
                                  [:td {:class (css-cls :pt-4)} (:alignment one-on-one)]
                                  [:td {:class (css-cls :pt-4)} (:development one-on-one)]
                                  [:td {:class (css-cls :pt-4)} (:next-steps one-on-one)]
                                  [:td {:class (css-cls :pt-4)} (:notes one-on-one)]
                                  [:td
                                   [:div {:class (css-cls :buttons :are-small :is-grouped)}
                                    [:button {:class (css-cls :button :is-danger :is-fixed-50)
                                              :on-click  #(show-confirm (grab :one-on-ones/confirm-delete) [::events/delete-item [@active-person one-on-one :one-on-ones :one-on-one-id]])} (grab :form/delete)]]]])]]])))