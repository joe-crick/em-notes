(ns em-notes.views.growth.metric 
  (:require [em-notes.components.fields.date-input :refer [set-date-input]]
            [em-notes.components.fields.radio-group :refer [radio-group]]
            [em-notes.components.fields.text-input :refer [set-text-input]]
            [em-notes.components.fields.textarea :refer [set-text-area]]
            [em-notes.components.form-footer :refer [form-footer]]
            [em-notes.events :as events]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.local-state :refer [local-state]]
            [em-notes.lib.text-to-bool :refer [text-to-bool]]
            [em-notes.subs :as subs]
            [re-frame.core :as rf]))

(defn metric []
  (let [active-metric (rf/subscribe [::subs/active-metric])
        active-person (rf/subscribe [::subs/active-person])
        [metric revise!] (local-state @active-metric)
        text-input (set-text-input metric revise!)
        date-input (set-date-input metric revise!)
        text-area (set-text-area metric revise!)]
    (fn []
      [:div.container
       [:div.is-hidden (:last-name @active-person)]
       [:h1 {:class "is-size-3"} (grab :metric/title)]
       [:form
        [text-input {:label (grab :metric/name)
                     :property [:name]}]
        [text-area {:label (grab :metric/details)
                    :property [:details]}]
        [date-input {:label (grab :metric/due-date)
                     :property [:due-date]}]
        
        [radio-group metric
         {:label (grab :metric/completed)
          :property [:completed]
          :values [[true (grab :metric/true)] [false (grab :metric/false)]]}
         text-to-bool]
        
        [form-footer (fn []
                       (rf/dispatch [::events/save-metric [@active-person @metric]])), #(rf/dispatch [::events/cancel-metric])]]]))
  )