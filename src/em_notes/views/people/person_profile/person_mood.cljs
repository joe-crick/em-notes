(ns em-notes.views.people.person-profile.person-mood
  (:require [em-notes.i18n.tr :refer [grab]]
            [em-notes.components.section-toggle :refer [section-toggle]]
            [em-notes.components.fields.text-input :refer [set-text-input]]
            [re-frame.core :as rf]
            [em-notes.subs :as subs]
            [em-notes.lib.revise :refer [set-revise]]))

(defn mood []
  (let [person (rf/subscribe [::subs/active-person])
        revise! (set-revise person)
        text-input (set-text-input person revise!)]
    (fn []
      [:fieldset
       [:legend (grab :person/mood)]
       [text-input {:label (grab :person/what-makes-grumpy)
                    :property [:profile :feedback :what-makes-grumpy]}]
       [text-input {:label (grab :person/how-to-know-grumpy)
                    :property [:profile :feedback :how-to-know-grumpy]}]
       [text-input {:label (grab :person/how-to-help-grumpy)
                    :property [:profile :feedback :how-to-help-grumpy]}]
       [text-input {:label (grab :person/three-hardest-things)
                    :property [:profile :feedback :three-hardest-things]}]])))

(defn person-mood []
  [section-toggle #(mood) (grab :person/mood)])