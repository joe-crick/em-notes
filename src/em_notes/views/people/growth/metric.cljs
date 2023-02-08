(ns em-notes.views.people.growth.metric
  (:require
   [em-notes.components.fields.select :refer [set-select]]
   [em-notes.components.fields.text-input :refer [set-text-input]]
   [em-notes.components.fields.textarea :refer [set-text-area]]
   [em-notes.components.form-footer :refer [form-footer]]
   [em-notes.events :as events]
   [em-notes.i18n.tr :refer [grab]]
   [em-notes.lib.local-state :refer [local-state]]
   [em-notes.subs :as subs]
   [re-frame.core :as rf]))

(defn metric []
  (let [active-growth-metric (rf/subscribe [::subs/active-growth-metric])
        active-person (rf/subscribe [::subs/active-person])
        [metric revise!] (local-state (if (nil? (:progress @active-growth-metric)) (assoc @active-growth-metric :progress (grab :growth-metric/begin)) @active-growth-metric))
        text-input (set-text-input metric revise!) 
        text-area (set-text-area metric revise!)
        select (set-select metric nil)]
    (fn []
      [:div.container
       [:div.is-hidden (:last-name @active-person)]
       [:form
        [text-input {:label (grab :growth-metric/name)
                     :property [:name]}]
        [text-area {:label (grab :growth-metric/details)
                    :property [:details]}]
        [select {:label (grab :growth-metric/progress)
                 :property [:progress]
                 :values [[nil (grab :growth-metric/begin)]
                          [nil (grab :growth-metric/in-progress)]
                          [nil (grab :growth-metric/achieved)]]}]
        [text-area {:label (grab :growth-metric/comments)
                    :property [:comments]}]

        [form-footer (fn []
                       (rf/dispatch [::events/save-item [@active-person @metric :growth-metrics :metric-id]])), #(rf/dispatch [::events/cancel-metric])]]])))