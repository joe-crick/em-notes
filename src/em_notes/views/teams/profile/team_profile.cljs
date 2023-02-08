(ns em-notes.views.teams.profile.team-profile
  (:require [em-notes.components.fields.switch-select :refer [switch-select]]
            [em-notes.components.fields.text-input :refer [set-text-input]]
            [em-notes.components.form-footer :refer [form-footer]]
            [em-notes.events :as events]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.bulma-cls :refer [bulma-cls]]
            [em-notes.lib.local-state :refer [local-state]]
            [em-notes.lib.person.get-person-id :refer [get-person-id]]
            [em-notes.lib.person.person-full-name :refer [person-full-name]]
            [em-notes.subs :as subs]
            [re-frame.core :as rf]))

(defn get-person-option [person]
  {:value (get-person-id person) :label (person-full-name person)})

(defn team-profile []
  (let [active-team (rf/subscribe [::subs/active-team])
        raw-people (rf/subscribe [::subs/people])
        people-vals (vals @raw-people)
        select-options (vec (map get-person-option people-vals))
        [team revise!] (local-state @active-team)
        text-input (set-text-input team revise!)]
    (fn []
      [:div.container
       [:h1 {:class (bulma-cls :subtitle)}
        (grab :team/profile)]
       [:div
        [:form
         [:fieldset
          [:legend (grab :team/profile)]
          [text-input {:label (grab :team/name)
                       :property [:name]}]
          [:div
           [switch-select team [:people] select-options (grab :team/avialable-people) (grab :team/members)]]
          [text-input {:label (grab :team/charter)
                       :property [:charter]}]
          [text-input {:label (grab :team/values)
                       :property [:values]}]]
         [form-footer #(rf/dispatch [::events/save-team @team]),
          #(rf/dispatch-sync [::events/reset-active-team])]]]])))