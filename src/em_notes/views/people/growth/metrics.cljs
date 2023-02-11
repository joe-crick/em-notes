(ns em-notes.views.people.growth.metrics
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
            [em-notes.views.people.growth.metric :refer [metric]]
            [re-frame.core :as rf]))

(defn metrics []
  (let [active-person (rf/subscribe [::subs/active-person]) 
        [filter revise!] (local-state { :filter ""})
        metric-view metric] 
    (fn []
      [:div.container
       [:div.is-hidden (:full-name @active-person)]
       [left-right (fn [] [:h1 {:class (css-cls :subtitle)}
                           (grab :growth-metrics/title)])
        (fn [] [:button {:class (css-cls :button :is-primary)
                         :on-click #(show-modal (grab :growth-metric/title) metric)} (grab :growth-metrics/create-metric)])]
       [table-filter filter revise!]
       [:table {:class (table-style)}
        [:thead
         [:tr
          [:th (grab :growth-metric/title)]
          [:th (grab :growth-metrics/details)]
          [:th (grab :growth-metrics/progress)]
          [:th (grab :table/actions)]]]
        [:tbody
         (for [metric (filter-map-on-prop (or (get-in @active-person [:data :growth-metrics]) []) [:name] (:filter @filter))
               :let [metric-id (:metric-id metric)]]
           ^{:key (random-uuid)} [:tr {:id metric-id}
                                  [:td.name {:data-progress (:progress metric)}
                                   [:button {:class (css-cls :button :is-ghost)
                                             :on-click #(rf/dispatch [::events/edit-metric [@active-person metric metric-view]])} (:name metric)]]
                                  [:td {:class (css-cls :pt-4)} (:details metric)]
                                  [:td {:class (css-cls :pt-4)} (str (:progress metric))]
                                  [:td
                                   [:div {:class (css-cls :buttons :are-small :is-grouped)}
                                    [:button {:class (css-cls :button :is-danger :is-fixed-50)
                                              :on-click  #(show-confirm (grab :growth-metrics/confirm-delete) [::events/delete-item [@active-person metric :growth-metrics :metric-id]])} (grab :form/delete)]]]])]]])))