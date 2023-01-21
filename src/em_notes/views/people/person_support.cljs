(ns em-notes.views.people.person-support
  (:require [em-notes.i18n.tr :refer [grab]]))

(defn person-support [person revise!]
  [:fieldset
   [:legend (grab :person/support)]
   [:div.field
    [:label (grab :person/support-manager)]
    [:p.control
     [:input.input {:html-for :manager
                    :type "text"
                    :id :manager,
                    :value (-> @person :support :manager)
                    :on-change #(revise! [:support :manager] %)}]]]
   [:div.field
    [:label (grab :person/support-team)]
    [:p.control
     [:input.input {:html-for :team
                    :type "text"
                    :id :team,
                    :value (-> @person :support :team)
                    :on-change #(revise! [:support :team] %)}]]]
   [:div.field
    [:label (grab :person/peers-outside)]
    [:p.control
     [:input.input {:html-for :peers-outside
                    :type "text"
                    :id :peers-outside,
                    :value (-> @person :support :peers-outside)
                    :on-change #(revise! [:support :peers-outside] %)}]]]])