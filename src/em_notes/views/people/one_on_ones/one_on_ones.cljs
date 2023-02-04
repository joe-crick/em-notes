(ns em-notes.views.people.one-on-ones.one-on-ones
  (:require [em-notes.components.left-right-cols :refer [left-right]]
            [em-notes.events :as events]
            [em-notes.subs :as subs]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.show-confirm :refer [show-confirm]]
            [em-notes.lib.show-modal :refer [show-modal]]
            [em-notes.lib.table-style :refer [table-style]] 
            [em-notes.views.people.one-on-ones.one-on-one :refer [one_on_one]]
            [re-frame.core :as rf]))

(defn one-on-ones []
  (let [active-person (rf/subscribe [::subs/active-person])
        one-on-ones (get-in @active-person [:data :one-on-ones])
        one-on-one-view one_on_one] 
    (fn []
      [:div.container
       [left-right (fn [] [:h1 {:class "subtitle"}
                           (grab :one-on-ones/title)])
        (fn [] [:button {:class "button is-primary"
                         :on-click #(show-modal (grab :one-on-one/title) one_on_one)} (grab :one-on-ones/create-one-on-one)])]
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
         (for [one-on-one one-on-ones
               :let [[_ one-on-one] one-on-one
                     one-on-one-id (:one-on-one-id one-on-one)]]
           ^{:key (random-uuid)} [:tr {:id one-on-one-id}
                                  [:td.name
                                   [:button {:class "button is-ghost"
                                             :on-click #(rf/dispatch [::events/edit-one-on-one [@active-person one-on-one one-on-one-view]])} (:week-of one-on-one)]]
                                  [:td {:class "pt-4"} (:perf one-on-one)]
                                  [:td {:class "pt-4"} (:alignment one-on-one)]
                                  [:td {:class "pt-4"} (:development one-on-one)]
                                  [:td {:class "pt-4"} (:next-steps one-on-one)]
                                  [:td {:class "pt-4"} (:notes one-on-one)]
                                  [:td
                                   [:div {:class "buttons are-small is-grouped"}
                                    [:button {:class "button is-danger is-fixed-50"
                                              :on-click  #(show-confirm (grab :one-on-ones/confirm-delete) [::events/delete-one-on-one [@active-person one-on-one]])} (grab :form/delete)]]]])]]])))