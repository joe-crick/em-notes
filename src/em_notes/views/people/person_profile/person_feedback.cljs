(ns em-notes.views.people.person-profile.person-feedback
  (:require [em-notes.i18n.tr :refer [grab]]
            [em-notes.components.section-toggle :refer [section-toggle]]
            [em-notes.components.fields.text-input :refer [set-text-input]]
            [re-frame.core :as rf]
            [em-notes.subs :as subs]
            [em-notes.lib.revise :refer [set-revise]]))

(defn feedback []
  (let [person (rf/subscribe [::subs/active-person])
        revise! (set-revise person)
        text-input (set-text-input person revise!)]
    (fn []
      [:fieldset
       [:legend (grab :person/feedback)]
       [text-input {:label (grab :person/feedback-medium)
                    :property [:profile :feedback :feedback-medium]}]
       [text-input {:label (grab :person/when-receive-feedback)
                    :property [:profile :feedback :when-receive-feedback]}]
       [text-input {:label (grab :person/how-receive-recognition)
                    :property [:profile :feedback :how-receive-recognition]}]
       [text-input {:label (grab :person/what-rewards-wanted)
                    :property [:profile :feedback :what-rewards-wanted]}]])))

(defn person-feedback []
  [section-toggle #(feedback) (grab :person/feedback)])