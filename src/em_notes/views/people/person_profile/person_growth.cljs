(ns em-notes.views.people.person-profile.person-growth
  (:require [em-notes.components.fields.text-input :refer [set-text-input]]
            [em-notes.components.section-toggle :refer [section-toggle]]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.revise :refer [get-revise!]]
            [em-notes.subs :as subs]
            [re-frame.core :as rf]))

(defn growth []
  (let [person (rf/subscribe [::subs/active-person])
        revise! (get-revise!)
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

(defn person-growth []
  [section-toggle #(growth) (grab :person/growth)])