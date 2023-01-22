(ns em-notes.views.people.person-growth
  (:require [em-notes.i18n.tr :refer [grab]]
            [em-notes.components.section-toggle :refer [section-toggle]]
            [em-notes.components.fields.text-input :refer [set-text-input]]))

(defn growth [person revise!]
  (let [text-input (set-text-input person revise!)]
    (fn []
      [:fieldset
       [:legend (grab :person/growth)]
       [text-input {:label (grab :person/current-goals)
                    :property [:growth :current-goals]}]
       [text-input {:label (grab :person/what-doing-now)
                    :property [:growth :what-doing-now]}]
       [text-input {:label (grab :person/what-need-to-do)
                    :property [:growth :what-need-to-do]}]])))

(defn person-growth [person revise!]
  [section-toggle #(growth person revise!) (grab :person/growth)])