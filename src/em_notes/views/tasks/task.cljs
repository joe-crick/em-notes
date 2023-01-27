(ns em-notes.views.tasks.task 
  (:require [em-notes.components.fields.text-input :refer [set-text-input]]
            [em-notes.components.fields.date-input :refer [set-date-input]]
            [em-notes.components.fields.textarea :refer [set-text-area]]
            [em-notes.components.form-footer :refer [form-footer]]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.local-state :refer [local-state]]
            [em-notes.subs :as subs]
            [em-notes.events :as events]
            [re-frame.core :as rf]))



(defn task []
  (let [active-task (rf/subscribe [::subs/active-task])
        [task revise!] (local-state @active-task)
        text-input (set-text-input task revise!)
        date-input (set-date-input task revise!)
        text-area (set-text-area task revise!)]
    (fn []
      [:div.container
       [:h1 {:class "is-size-3"} (grab :task/title)]
       [:form
        [text-input {:label (grab :task/name)
                     :property [:name]}]
        [text-area {:label (grab :task/details)
                     :property [:details]}]
        [date-input {:label (grab :task/due-date)
                     :property [:due-date]}]
        [text-input {:label (grab :task/completed)
                     :property [:completed]}]
        [form-footer (fn []
                       (rf/dispatch [::events/save-task @task])), #(rf/dispatch [::events/cancel-task])]]]))
  )