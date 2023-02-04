(ns em-notes.views.people.person-profile.person-details
  (:require [em-notes.components.fields.text-input :refer [set-text-input]]
            [em-notes.components.section-toggle :refer [section-toggle]]
            [em-notes.events :as events]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.get-evt-val :refer [get-evt-val]]
            [em-notes.subs :as subs]
            [re-frame.core :as rf]))

(defn details [] 
  (let [person (rf/subscribe [::subs/active-person])
        revise! (fn [_ evt] 
                  (let [value (get-evt-val evt)]
                    (rf/dispatch [::events/set-person-field value])))
        text-input (set-text-input person revise!)]
    (fn []
      [:fieldset
       [:legend (grab :person/details)]
       [text-input {:label (grab :person/first-name)
                    :property [:first-name]}]
       [text-input {:label (grab :person/last-name)
                    :property [:last-name]}]
       [text-input {:label (grab :person/team)
                    :property [:team]}]])))

(defn person-details []
  [section-toggle #(details) (grab :person/details) true])