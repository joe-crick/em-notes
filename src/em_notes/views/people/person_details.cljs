(ns em-notes.views.people.person-details
  (:require [em-notes.i18n.tr :refer [grab]]))

(defn person-details [person revise!]
  [:fieldset
   [:legend (grab :person/details)]
   [:div.field
    [:label  (grab :person/first-name)]
    [:p.control
     [:input.input {:html-for :first-name
                    :type "text"
                    :id :first-name,
                    :value (:first-name @person)
                    :on-change #(revise! :first-name %)}]]]
   [:div.field
    [:label (grab :person/last-name)]
    [:p.control
     [:input.input {:html-for :last-name
                    :type "text"
                    :id :last-name,
                    :value (:last-name @person)
                    :on-change #(revise! :last-name %)}]]]
   [:div.field
    [:label (grab :person/team)]
    [:p.control
     [:input.input {:html-for :team
                    :type "text",
                    :id :team
                    :value (:team @person)
                    :on-change #(revise! :team %)}]]]]
  )