(ns em-notes.views.performance.perf
  (:require [em-notes.components.fields.text-input :refer [set-text-input]]
            [em-notes.components.fields.date-input :refer [set-date-input]]
            [em-notes.components.fields.textarea :refer [set-text-area]]
            [em-notes.components.form-footer :refer [form-footer]]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.local-state :refer [local-state]]
            [em-notes.subs :as subs]
            [em-notes.events :as events]
            [re-frame.core :as rf]))

(defn performance []
  (let [active-perf (rf/subscribe [::subs/active-perf])
        [perf revise!] (local-state @active-perf)
        text-input (set-text-input perf revise!)
        date-input (set-date-input perf revise!)
        text-area (set-text-area perf revise!)]
    (fn []
      [:div.container
       [:h1 {:class "is-size-3"} (grab :perf/title)]
       [:form
        [text-input {:label (grab :perf/name)
                     :property [:name]}]
        [text-area {:label (grab :perf/details)
                    :property [:details]}]
        [date-input {:label (grab :perf/due-date)
                     :property [:due-date]}]
        [text-input {:label (grab :perf/completed)
                     :property [:completed]}]
        [form-footer (fn []
                       (rf/dispatch [::events/save-perf @perf])), #(rf/dispatch [::events/cancel-perf])]]]))
  )