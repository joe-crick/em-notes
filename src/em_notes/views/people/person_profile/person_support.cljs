(ns em-notes.views.people.person-profile.person-support
  (:require [em-notes.components.fields.text-input :refer [set-text-input]]
            [em-notes.components.section-toggle :refer [section-toggle]]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.revise :refer [get-revise!]]
            [em-notes.subs :as subs]
            [re-frame.core :as rf]))

(defn support []
  (let [person (rf/subscribe [::subs/active-person])
        revise! (get-revise!)
        text-input (set-text-input person revise!)]
    (fn []
      [:fieldset
       [:legend (grab :person/support)]
       [text-input {:label (grab :person/support-manager)
                    :property [:profile :support :manager]}]
       [text-input {:label (grab :person/support-team)
                    :property [:profile :support :team]}]
       [text-input {:label (grab :person/peers-outside)
                    :property [:profile :support :peers-outside]}]])))

(defn person-support []
  [section-toggle #(support) (grab :person/support)])