(ns em-notes.views.teams.profile.team-profile
  (:require [em-notes.components.fields.text-input :refer [set-text-input]]
            [em-notes.components.form-footer :refer [form-footer]]
            [em-notes.events :as events]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.css-cls :refer [css-cls]]
            [em-notes.lib.local-state :refer [local-state]]
            [em-notes.subs :as subs]
            [re-frame.core :as rf]))



(defn team-profile []
  (let [active-team (rf/subscribe [::subs/active-team])
        [team revise!] (local-state @active-team)
        text-input (set-text-input team revise!)]
    (fn []
      [:div.container
       [:h1 {:class (css-cls :subtitle)}
        (grab :team/profile)]
       [:div
        [:form
         [:fieldset
          [:legend (grab :team/profile)]
          [text-input {:label (grab :team/name)
                       :property [:name]}] 
          [text-input {:label (grab :team/charter)
                       :property [:charter]}]
          [text-input {:label (grab :team/values)
                       :property [:values]}]
          [:div.field
           [:label (grab :team/people)]
           [:ul.bullet-list
            (for [person (:people @active-team)]
              [:li (:label person)])]]]
         [form-footer #(rf/dispatch [::events/save-team @team]),
          #(rf/dispatch-sync [::events/reset-active-team])]]]])))