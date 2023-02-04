(ns em-notes.views.people.person-profile.person-growth
  (:require [em-notes.i18n.tr :refer [grab]]
            [em-notes.components.section-toggle :refer [section-toggle]]
            [em-notes.components.fields.text-input :refer [set-text-input]]
            [re-frame.core :as rf]
            [em-notes.subs :as subs]
            [em-notes.lib.revise :refer [set-revise]]))

(defn growth []
  (let [person (rf/subscribe [::subs/active-person])
        revise! (set-revise person)
        text-input (set-text-input person revise!)]
    (fn []
      [:fieldset
       [:legend (grab :person/growth)]
       [text-input {:label (grab :person/current-goals)
                    :property [:profile :growth :current-goals]}]
       [text-input {:label (grab :person/what-doing-now)
                    :property [:profile :growth :what-doing-now]}]
       [text-input {:label (grab :person/what-need-to-do)
                    :property [:profile :growth :what-need-to-do]}]])))

(defn person-growth [person revise!]
  [section-toggle #(growth person revise!) (grab :person/growth)])