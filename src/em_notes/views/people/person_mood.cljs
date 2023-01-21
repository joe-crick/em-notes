(ns em-notes.views.people.person-mood
  (:require [em-notes.i18n.tr :refer [grab]]))

(defn person-mood [person revise!]
  [:fieldset
   [:legend (grab :person/mood)]
   [:div.field
    [:label (grab :person/what-makes-grumpy)]
    [:p.control
     [:input.input {:html-for :what-makes-grumpy
                    :type "text"
                    :id :what-makes-grumpy,
                    :value (-> @person :mood :what-makes-grumpy)
                    :on-change #(revise! [:mood :what-makes-grumpy] %)}]]]
   [:div.field
    [:label (grab :person/how-to-know-grumpy)]
    [:p.control
     [:input.input {:html-for :how-to-know-grumpy
                    :type "text"
                    :id :how-to-know-grumpy,
                    :value (-> @person :mood :how-to-know-grumpy)
                    :on-change #(revise! [:mood :how-to-know-grumpy] %)}]]]
   [:div.field
    [:label (grab :person/how-to-help-grumpy)]
    [:p.control
     [:input.input {:html-for :how-to-help-grumpy
                    :type "text"
                    :id :how-to-help-grumpy,
                    :value (-> @person :mood :how-to-help-grumpy)
                    :on-change #(revise! [:mood :how-to-help-grumpy] %)}]]]])