(ns em-notes.views.performance.perfs
  (:require [em-notes.components.left-right-cols :refer [left-right]]
            [em-notes.events :as events]
            [em-notes.subs :as subs]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.show-confirm :refer [show-confirm]]
            [em-notes.lib.show-modal :refer [show-modal]]
            [em-notes.lib.table-style :refer [table-style]] 
            [em-notes.views.performance.perf :as perf]
            [re-frame.core :as rf]))

(defn perfs []
  (let [active-person (rf/subscribe [::subs/active-person])
        perfs (get-in @active-person [:data :perfs])] 
    (fn []
      [:div.container
       [left-right (fn [] [:h1 {:class "subtitle"}
                           (grab :perf/title)])
        (fn [] [:button {:class "button is-primary"
                         :on-click #(show-modal (grab :perf/title) perf/perf)} (grab :perfs/create-perf)])]
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
         (for [perf perfs
               :let [[_ perf] perf
                     perf-id (:perf-id perf)]]
           ^{:key (random-uuid)} [:tr {:id perf-id}
                                  [:td.name
                                   [:button {:class "button is-ghost"
                                             :on-click #(rf/dispatch [::events/edit-perf [@active-person perf perf/perf]])} (:week perf)]]
                                  [:td {:class "pt-4"} (:velocity perf)]
                                  [:td {:class "pt-4"} (str (:prs perf))]
                                  [:td {:class "pt-4"} (str (:collaboration perf))]
                                  [:td {:class "pt-4"} (str (:avg-est-accuracy perf))]
                                  [:td {:class "pt-4"} (str (:notes perf))]
                                  [:td  
                                   [:div {:class "buttons are-small is-grouped"} 
                                    [:button {:class "button is-danger is-fixed-50"
                                              :on-click  #(show-confirm (grab :perf/confirm-delete) [::events/delete-perf [@active-person perf]])} (grab :form/delete)]]]])]]])))