(ns em-notes.views.performance.perf
  (:require [em-notes.components.fields.date-input :refer [set-date-input]]
            [em-notes.components.fields.text-input :refer [set-text-input]]
            [em-notes.components.fields.textarea :refer [set-text-area]]
            [em-notes.components.form-footer :refer [form-footer]]
            [em-notes.events :as events]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.local-state :refer [local-state]]
            [em-notes.subs :as subs]
            [re-frame.core :as rf]))

(defn perf []
  (let [active-perf (rf/subscribe [::subs/active-perf])
        active-person (rf/subscribe [::subs/active-person])
        [perf revise!] (local-state (assoc @active-perf :progress (grab :perf/begin)))
        text-input (set-text-input perf revise!)
        text-area (set-text-area perf revise!)
        date-input (set-date-input perf revise!)]
    (fn []
      [:div.container
       [:div.is-hidden (:last-name @active-person)]
       [:form
        [date-input {:label (grab :perf/week)
                     :property [:week]}]
        [text-input {:label (grab :perf/velocity)
                     :property [:velocity]}]
        [text-input {:label (grab :perf/prs)
                     :property [:prs]}]
        [text-input {:label (grab :perf/collaboration)
                     :property [:collaboration]}]
        [text-input {:label (grab :perf/avg-est-accuracy)
                     :property [:avg-est-accuracy]}]
        [text-area {:label (grab :perf/notes)
                    :property [:notes]}]

        [form-footer (fn []
                       (rf/dispatch [::events/save-item [@active-person @perf :perfs :perf-id]])), #(rf/dispatch [::events/cancel-perf])]]])))