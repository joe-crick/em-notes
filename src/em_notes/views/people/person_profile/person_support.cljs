(ns em-notes.views.people.person-profile.person-support
  (:require
   [em-notes.i18n.tr :refer [grab]]
   [em-notes.components.section-toggle :refer [section-toggle]]
   [em-notes.components.fields.text-input :refer [set-text-input]]
   [re-frame.core :as rf]
   [em-notes.subs :as subs]
   [em-notes.lib.revise :refer [set-revise]]))

(defn support []
  (let [person (rf/subscribe [::subs/active-person])
        revise! (set-revise person)
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

(defn person-support [person revise!]
  [section-toggle #(support person revise!) (grab :person/support)])