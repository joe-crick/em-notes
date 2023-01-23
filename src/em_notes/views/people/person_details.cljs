(ns em-notes.views.people.person-details
  (:require [em-notes.i18n.tr :refer [grab]]
            [em-notes.components.section-toggle :refer [section-toggle]]
            [em-notes.components.fields.text-input :refer [set-text-input]]))

(defn details [person revise!] 
  (let [text-input (set-text-input person revise!)]
    (fn []
      [:fieldset
       [:legend (grab :person/details)]
       [text-input {:label (grab :person/first-name)
                    :property [:first-name]}]
       [text-input {:label (grab :person/last-name)
                    :property [:last-name]}]
       [text-input {:label (grab :person/team)
                    :property [:team]}]])))

(defn person-details [person revise!]
  [section-toggle #(details person revise!) (grab :person/details) true])