(ns em-notes.views.tasks.task
  (:require [em-notes.components.fields.date-input :refer [set-date-input]]
            [em-notes.components.fields.radio-group :refer [radio-group]]
            [em-notes.components.fields.select :refer [set-select]]
            [em-notes.components.fields.text-input :refer [set-text-input]]
            [em-notes.components.fields.textarea :refer [set-text-area]]
            [em-notes.components.form-footer :refer [form-footer]]
            [em-notes.events :as events]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.local-state :refer [local-state]]
            [em-notes.lib.text-to-bool :refer [text-to-bool]]
            [em-notes.subs :as subs] 
            [re-frame.core :as rf]
            [reagent.core :as r]))

(defn revise-person [atom _ evt]
  (let [owner-id (.. evt -target -value)]
    (rf/dispatch [::events/get-active-person owner-id])
    (swap! atom assoc :owner-id owner-id)))

(defn task []
  (let [active-task (rf/subscribe [::subs/active-task])
        active-context (rf/subscribe [::subs/active-context])
        active-entity (rf/subscribe [::subs/active-entity @active-context])
        [task revise!] (local-state @active-task)
        text-input (set-text-input task revise!)
        date-input (set-date-input task revise!)
        text-area (set-text-area task revise!)
        raw-people (rf/subscribe [::subs/people])
        context (if (= @active-context :teams) "team" "person")
        entity (r/atom {:owner-id ((keyword (str context "-id")) @active-entity)})
        revise-entity! (partial revise-person entity)
        select (set-select entity revise-entity!)
        select-options (map (fn [p]
                              [(:person-id p) (:full-name p)]) (vals @raw-people))]
    (fn []
      [:div.container
       [:div.is-hidden (:last-name @active-entity)]
       [:form
        (if-not (= @active-context :teams)
          [select {:label (grab :task/person)
                 :property [:owner-id]
                 :values select-options}]
          [:div.is-hidden])
        [text-input {:label (grab :task/name)
                     :property [:name]}]
        [text-area {:label (grab :task/details)
                    :property [:details]}]
        [date-input {:label (grab :task/due-date)
                     :property [:due-date]}]

        [radio-group task
         {:label (grab :task/completed)
          :property [:completed]
          :values [[true (grab :task/true)] [false (grab :task/false)]]}
         text-to-bool]

        [form-footer (fn []
                       (rf/dispatch [::events/save-task [@active-entity @task context]])), #(rf/dispatch [::events/cancel-task])]]])))