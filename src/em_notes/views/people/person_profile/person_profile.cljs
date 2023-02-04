(ns em-notes.views.people.person-profile.person-profile
  (:require
   [em-notes.i18n.tr :refer [grab]]
   [em-notes.components.section-toggle :refer [section-toggle]]
   [em-notes.components.fields.text-input :refer [set-text-input]]
   [em-notes.components.fields.select :refer [set-select]]
   [re-frame.core :as rf]
   [em-notes.subs :as subs]
   [em-notes.lib.revise :refer [set-revise]]))

(defn profile [] 
  (let [person (rf/subscribe [::subs/active-person])
        revise! (set-revise person)
        text-input (set-text-input person revise!)
        select (set-select person revise!)]
    (fn []
      [:fieldset
       [:legend (grab :person/details)]
       [select {:label (grab :person/attachment-style)
                :property [:attachment-style]
                :values [[nil (grab :person/secure)]
                         [nil (grab :person/anxious)]
                         [nil (grab :person/avoidant)]
                         [nil (grab :person/disorganised)]]}]
       [:div
        {:class "columns"}
        [:div {:class "column"}
         [text-input {:label (grab :person/openness)
                      :property [:profile :openness]}]]
        [:div {:class "column"}
         [text-input {:label (grab :person/conscientiousness)
                      :property [:profile :conscientiousness]}]]
        [:div {:class "column"}
         [text-input {:label (grab :person/attachment-style)
                      :property [:profile :extroversion]}]]
        [:div {:class "column"}
         [text-input {:label (grab :person/agreableness)
                      :property [:profile :agreableness]}]]
        [:div {:class "column"}
         [text-input {:label (grab :person/neuroticism)
                      :property [:profile :neuroticism]}]]]])))

(defn person-profile []
  [section-toggle #(profile) (grab :person/profile) false])