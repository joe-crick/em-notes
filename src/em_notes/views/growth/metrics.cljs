(ns em-notes.views.growth.metrics
  (:require [em-notes.components.left-right-cols :refer [left-right]]
            [em-notes.events :as events]
            [em-notes.subs :as subs]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.show-confirm :refer [show-confirm]]
            [em-notes.lib.show-modal :refer [show-modal]]
            [em-notes.lib.table-style :refer [table-style]] 
            [em-notes.views.growth.metric :refer [metric]]
            [re-frame.core :as rf]))

(defn metrics []
  (let [active-person (rf/subscribe [::subs/active-person])
        metrics (:growth-metrics @active-person)
        metric-view metric] 
    (fn []
      [:div.container
       [left-right (fn [] [:h1 {:class "subtitle"}
                           (grab :growth-metrics/title)])
        (fn [] [:button {:class "button is-primary"
                         :on-click #(show-modal (grab :growth-metric/title) metric)} (grab :growth-metrics/create-metric)])]
       [:table {:class (table-style)}
        [:thead
         [:tr
          [:th (grab :growth-metrics/title)]
          [:th (grab :growth-metrics/details)]
          [:th (grab :growth-metrics/progress)]
          [:th (grab :table/actions)]]]
        [:tbody
         (for [metric metrics
               :let [[_ metric] metric
                     metric-id (:metric-id metric)]]
           ^{:key (random-uuid)} [:tr {:id metric-id}
                                  [:td.name
                                   [:button {:class "button is-ghost"
                                             :on-click #(rf/dispatch [::events/edit-metric [@active-person metric metric-view]])} (:name metric)]]
                                  [:td {:class "pt-4"} (:details metric)]
                                  [:td {:class "pt-4"} (str (:completed metric))]
                                  [:td
                                   [:div {:class "buttons are-small is-grouped"}
                                    [:button {:class "button is-danger is-fixed-50"
                                              :on-click  #(show-confirm (grab :growth-metrics/confirm-delete) [::events/delete-metric [@active-person metric]])} (grab :form/delete)]]]])]]])))