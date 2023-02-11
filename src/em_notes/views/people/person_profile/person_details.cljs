(ns em-notes.views.people.person-profile.person-details
  (:require [em-notes.components.fields.select :refer [set-select]]
            [em-notes.components.fields.text-input :refer [set-text-input]]
            [em-notes.components.section-toggle :refer [section-toggle]]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.revise :refer [get-revise!]]
            [em-notes.subs :as subs]
            [re-frame.core :as rf]))

(defn details [] 
  (let [person (rf/subscribe [::subs/active-person])
        revise! (get-revise!)
        text-input (set-text-input person revise!)
        select (set-select person revise!)
        teams (rf/subscribe [::subs/teams])
        select-options (map (fn [t]
                              [(:team-id t) (:name t)]) (vals @teams))]
    (fn []
      [:fieldset
       [:legend (grab :person/details)]
       [text-input {:label (grab :person/first-name)
                    :property [:first-name]}]
       [text-input {:label (grab :person/last-name)
                    :property [:last-name]}]
       [select {:label (grab :person/team)
                :property [:team]
                :values select-options}]])))

(defn person-details []
  [section-toggle #(details) (grab :person/details) true])