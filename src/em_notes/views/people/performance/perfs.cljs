(ns em-notes.views.people.performance.perfs
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
            [em-notes.views.performance.perf :as perf]
            [re-frame.core :as rf]))

(defn perfs []
  (let [active-person (rf/subscribe [::subs/active-person])
        [filter revise!] (local-state {:filter ""})] 
    (fn []
      [:div.container
       [left-right (fn [] [:h1 {:class (css-cls :subtitle)}
                           (grab :perf/title)])
        (fn [] [:button {:class (css-cls :button :is-primary)
                         :on-click #(show-modal (grab :perf/title) perf/perf)} (grab :perfs/create-perf)])]
       [table-filter filter revise!]
       [:table {:class (table-style)}
        [:thead
         [:tr
          [:th (grab :perf/title)]
          [:th (grab :perf/velocity)]
          [:th (grab :perf/prs)]
          [:th (grab :perf/collaboration)]
          [:th (grab :perfs/estimation)]
          [:th (grab :perf/notes)]
          [:th (grab :table/actions)]]]
        [:tbody
         (for [perf (filter-map-on-prop (or (get-in @active-person [:data :perfs]) []) [:week] (:filter @filter))
               :let [perf-id (:perf-id perf)]]
           ^{:key (random-uuid)} [:tr {:id perf-id}
                                  [:td.name
                                   [:button {:class (css-cls :button :is-ghost)
                                             :on-click #(rf/dispatch [::events/edit-perf [@active-person perf perf/perf]])} (:week perf)]]
                                  [:td {:class (css-cls :pt-4)} (:velocity perf)]
                                  [:td {:class (css-cls :pt-4)} (str (:prs perf))]
                                  [:td {:class (css-cls :pt-4)} (str (:collaboration perf))]
                                  [:td {:class (css-cls :pt-4)} (str (:avg-est-accuracy perf))]
                                  [:td {:class (css-cls :pt-4)} (str (:notes perf))]
                                  [:td
                                   [:div {:class (css-cls :buttons :are-small :is-grouped)}
                                    [:button {:class (css-cls :button :is-danger :is-fixed-50)
                                              :on-click  #(show-confirm (grab :perf/confirm-delete) [::events/delete-item [@active-person perf :perfs :perf-id]])} (grab :form/delete)]]]])]]])))