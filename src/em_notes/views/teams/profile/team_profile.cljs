(ns em-notes.views.teams.profile.team-profile
  (:require [em-notes.components.fields.select :refer [set-select]]
            [em-notes.components.fields.text-input :refer [set-text-input]]
            [em-notes.components.form-footer :refer [form-footer]]
            [em-notes.events :as events]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.get-person-id :refer [get-person-id]]
            [em-notes.lib.local-state :refer [local-state]]
            [em-notes.subs :as subs]
            [re-frame.core :as rf]))

(defn get-person [person]
  [(get-person-id person) (str (:first-name person) " " (:last-name person))]
  )

(defn team-profile [query]
  (let [active-team (rf/subscribe [::subs/active-team])
        raw-people (rf/subscribe [::subs/people])
        people (map get-person (vals @raw-people))
        [team revise!] (local-state (if (nil? query) (assoc @active-team :people []) @active-team))
        text-input (set-text-input team revise!)
        select (set-select team revise!)
        ]
    (fn []
      [:div.container
       [:h1 {:class "subtitle"}
        (grab :team/profile)]
       [:div
        [:form
         [:fieldset
          [:legend (grab :team/profile)]
          [text-input {:label (grab :team/name)
                       :property [:name]}]
          [select {:label (grab :team/people)
                   :property [:people]
                   :multi? true
                   :default-value []
                   :values people}]
         [text-input {:label (grab :team/charter)
                      :property [:charter]}]
         [text-input {:label (grab :team/values)
                      :property [:values]}]]
         [form-footer #(rf/dispatch [::events/save-team @team]),
          #(rf/dispatch-sync [::events/reset-active-team])]]]])))