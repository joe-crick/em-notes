(ns em-notes.views.people.person-growth
  (:require [em-notes.i18n.tr :refer [grab]]
            [em-notes.components.section-toggle :refer [section-toggle]]))

(defn growth [person revise!]
  [:fieldset
   [:legend (grab :person/growth)]
   [:div.field
    [:label (grab :person/current-goals)]
    [:p.control
     [:input.input {:html-for :current-goals
                    :type "text"
                    :id :current-goals,
                    :value (-> @person :growth :current-goals)
                    :on-change #(revise! [:growth :current-goals] %)}]]]
   [:div.field
    [:label (grab :person/support-team)]
    [:p.control
     [:input.input {:html-for :what-doing-now
                    :type "text"
                    :id :what-doing-now,
                    :value (-> @person :growth :what-doing-now)
                    :on-change #(revise! [:growth :what-doing-now] %)}]]]
   [:div.field
    [:label (grab :person/what-need-to-do)]
    [:p.control
     [:input.input {:html-for :what-need-to-do
                    :type "text"
                    :id :what-need-to-do,
                    :value (-> @person :growth :what-need-to-do)
                    :on-change #(revise! [:growth :what-need-to-do] %)}]]]]
  )

(defn person-growth [person revise!]
  [section-toggle #(growth person revise!) (grab :person/growth)])