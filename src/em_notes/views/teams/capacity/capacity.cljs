(ns em-notes.views.teams.capacity.capacity
  (:require [em-notes.components.fields.date-input :refer [set-date-input]]
            [em-notes.components.fields.number-input :refer [set-number-input]]
            [em-notes.components.fields.textarea :refer [set-text-area]]
            [em-notes.components.form-footer :refer [form-footer]]
            [em-notes.events :as events]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.local-state :refer [local-state]]
            [em-notes.subs :as subs]
            [re-frame.core :as rf]))

(defn capacity []
  (let [active-capacity (rf/subscribe [::subs/active-capacity])
        active-team (rf/subscribe [::subs/active-team])
        [capacity revise!] (local-state @active-capacity)
        text-area (set-text-area capacity revise!)
        number-input (set-number-input capacity revise!)
        date-input (set-date-input capacity revise!)]
    (fn []
      [:div.container
       [:div.is-hidden (:last-name @active-team)]
       [:form
        [date-input {:label (grab :capacity/week)
                     :property [:week-of]}]
        [number-input {:label (grab :capacity/percent-capacity)
                    :property [:percent-capacity]}]
        [text-area {:label (grab :capacity/notes)
                    :property [:notes]}]

        [form-footer (fn []
                       (rf/dispatch [::events/save-capacity [@active-team @capacity]])), #(rf/dispatch [::events/cancel-capacity])]]])))