(ns em-notes.views.people.person-profile.person-mood
  (:require [em-notes.i18n.tr :refer [grab]]
            [em-notes.components.section-toggle :refer [section-toggle]]
            [em-notes.components.fields.text-input :refer [set-text-input]]))

(defn mood [person revise!]
  (let [text-input (set-text-input person revise!)]
   (fn []
     [:fieldset
      [:legend (grab :person/mood)]
      [text-input {:label (grab :person/what-makes-grumpy)
                   :property [:feedback :what-makes-grumpy]}]
      [text-input {:label (grab :person/how-to-know-grumpy)
                   :property [:feedback :how-to-know-grumpy]}]
      [text-input {:label (grab :person/how-to-help-grumpy)
                   :property [:feedback :how-to-help-grumpy]}]])))

(defn person-mood [person revise!]
  [section-toggle #(mood person revise!) (grab :person/mood)])