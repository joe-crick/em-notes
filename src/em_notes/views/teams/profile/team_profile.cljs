(ns em-notes.views.teams.profile.team-profile
  (:require [em-notes.components.fields.select :refer [set-select]]
            [em-notes.components.fields.switch-select :refer [switch-select]]
            [em-notes.components.fields.text-input :refer [set-text-input]]
            [em-notes.components.form-footer :refer [form-footer]]
            [em-notes.events :as events]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.bulma-cls :refer [bulma-cls]]
            [em-notes.lib.local-state :refer [local-state]]
            [em-notes.lib.person.get-person-id :refer [get-person-id]]
            [em-notes.subs :as subs]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(defn get-person [person]
  [(get-person-id person) (str (:first-name person) " " (:last-name person))])


(def select-options [{:value "option1" :label "Option 1"}
                     {:value "option2" :label "Option 2"}
                     {:value "option3" :label "Option 3"}
                     {:value "option4" :label "Option 4"}
                     {:value "option5" :label "Option 5"}])

(def select-value (r/atom {:values [{:value "option1" :label "Option 1"}]}))

(defn team-profile []
  (let [active-team (rf/subscribe [::subs/active-team])
        raw-people (rf/subscribe [::subs/people])
        people (map get-person (vals @raw-people))
        [team revise!] (local-state @active-team)
        text-input (set-text-input team revise!)
        select (set-select team nil)]
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
           [:h2 "Switch Select Example"]
           [switch-select select-value select-options]]
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