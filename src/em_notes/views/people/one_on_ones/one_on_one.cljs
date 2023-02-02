(ns em-notes.views.people.one-on-ones.one-on-one
  (:require
   [em-notes.components.fields.textarea :refer [set-text-area]]
   [em-notes.components.fields.date-input :refer [set-date-input]]
   [em-notes.components.form-footer :refer [form-footer]]
   [em-notes.events :as events]
   [em-notes.i18n.tr :refer [grab]]
   [em-notes.lib.local-state :refer [local-state]]
   [em-notes.subs :as subs]
   [re-frame.core :as rf]))

(defn one_on_one []
  (let [active-one-on-one (rf/subscribe [::subs/active-one-on-one])
        active-person (rf/subscribe [::subs/active-person])
        [one-on-one revise!] (local-state (if (nil? (:progress @active-one-on-one)) (assoc @active-one-on-one :progress (grab :one-on-one/begin)) @active-one-on-one)) 
        text-area (set-text-area one-on-one revise!)
        date-input (set-date-input one-on-one revise!)]
    (fn []
      [:div.container
       [:div.is-hidden (:last-name @active-person)]
       [:form
        [date-input {:label (grab :one-on-one/week-of)
                     :property [:week-of]}]
        [text-area {:label (grab :one-on-one/perf)
                    :property [:perf]}]
        [text-area {:label (grab :one-on-one/alignment)
                     :property [:alignment]}]
        [text-area {:label (grab :one-on-one/development)
                    :property [:development]}]
        [text-area {:label (grab :one-on-one/next-steps)
                    :property [:next-steps]}]
        [text-area {:label (grab :one-on-one/notes)
                    :property [:notes]}]

        [form-footer (fn []
                       (rf/dispatch [::events/save-one-on-one [@active-person @one-on-one]])), #(rf/dispatch [::events/cancel-one-on-one])]]])))