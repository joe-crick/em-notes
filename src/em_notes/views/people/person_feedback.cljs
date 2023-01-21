(ns em-notes.views.people.person-feedback
  (:require [em-notes.i18n.tr :refer [grab]]))

(defn person-feedback [person revise!]
  [:fieldset
   [:legend (grab :person/feedback)]
   [:div.field
    [:label (grab :person/feedback-medium)]
    [:p.control
     [:input.input {:html-for :feedback-medium
                    :type "text"
                    :id :feedback-medium,
                    :value (-> @person :feedback :feedback-medium)
                            ;; need to be able to update-in
                    :on-change #(revise! [:feedback :feedback-medium] %)}]]]
   [:div.field
    [:label (grab :person/when-receive-feedback)]
    [:p.control
     [:input.input {:html-for :when-receive-feedback
                    :type "text"
                    :id :when-receive-feedback,
                    :value (-> @person :feedback :when-receive-feedback)
                    :on-change #(revise! [:feedback :when-receive-feedback] %)}]]]
   [:div.field
    [:label (grab :person/how-receive-recognition)]
    [:p.control
     [:input.input {:html-for :how-receive-recognition
                    :type "text"
                    :id :how-receive-recognition,
                    :value (-> @person :feedback :how-receive-recognition)
                    :on-change #(revise! [:feedback :how-receive-recognition] %)}]]]

   [:div.field
    [:label (grab :person/what-rewards-wanted)]
    [:p.control
     [:input.input {:html-for :what-rewards-wanted
                    :type "text"
                    :id :what-rewards-wanted,
                    :value (-> @person :feedback :what-rewards-wanted)
                    :on-change #(revise! :what-rewards-wanted %)}]]]])