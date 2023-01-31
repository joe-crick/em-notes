(ns em-notes.views.growth.metric
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
  (let [active-metric (rf/subscribe [::subs/active-metric])
        active-person (rf/subscribe [::subs/active-person])
        [metric revise!] (local-state @active-metric)
        text-input (set-text-input metric revise!) 
        text-area (set-text-area metric revise!)
        select (set-select metric revise!)]
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
                 :values [[0 (grab :growth-metric/begin)]
                          [1 (grab :growth-metric/in-progress)]
                          [2 (grab :growth-metric/achieved)]]}]
        [text-area {:label (grab :growth-metric/comments)
                    :property [:comments]}]

        [form-footer (fn []
                       (rf/dispatch [::events/save-metric [@active-person @metric]])), #(rf/dispatch [::events/cancel-metric])]]])))