(ns em-notes.views.people.person-profile.person-feedback
  (:require [em-notes.i18n.tr :refer [grab]]
            [em-notes.components.section-toggle :refer [section-toggle]]
            [em-notes.components.fields.text-input :refer [set-text-input]]))

(defn feedback [person revise!]
  (let [text-input (set-text-input person revise!)]
    (fn []
      [:fieldset
       [:legend (grab :person/feedback)]
       [text-input {:label (grab :person/feedback-medium)
                    :property [:feedback :feedback-medium]}]
       [text-input {:label (grab :person/when-receive-feedback)
                    :property [:feedback :when-receive-feedback]}]
       [text-input {:label (grab :person/how-receive-recognition)
                    :property [:feedback :how-receive-recognition]}]
       [text-input {:label (grab :person/what-rewards-wanted)
                    :property [:feedback :what-rewards-wanted]}]])))

(defn person-feedback [person revise!]
  [section-toggle #(feedback person revise!) (grab :person/feedback)])