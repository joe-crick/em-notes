(ns em-notes.components.confirmation
  (:require [em-notes.components.modal :refer [modal]]
            [em-notes.events :as events]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.css-cls :refer [css-cls]]
            [re-frame.core :as rf]))

(defn close-confirm []
  (rf/dispatch [::events/close-confirm]))

(defn confirm [display msg on-confirm] 
  [modal display (grab :confirm/title)
   (fn []
     [:div
      [:p.mb-5 msg]
      [:div {:class (css-cls :container :is-flex :is-justify-content-space-between)}
       [:div
        [:button {:class (css-cls :button :is-primary) :on-click #(rf/dispatch [::events/run-confirm on-confirm])} (grab :confirm/yes)]]
       [:div
        [:button {:class (css-cls :button :is-info)
                  :aria-label "close"
                  :on-click close-confirm} (grab :form/cancel)]]]]) close-confirm])