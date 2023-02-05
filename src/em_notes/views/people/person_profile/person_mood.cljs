(ns em-notes.views.people.person-profile.person-mood
  (:require [em-notes.components.fields.text-input :refer [set-text-input]]
            [em-notes.components.section-toggle :refer [section-toggle]]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.revise :refer [get-revise!]]
            [em-notes.subs :as subs]
            [re-frame.core :as rf]))

(defn mood []
  (let [person (rf/subscribe [::subs/active-person])
        revise! (get-revise!)
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