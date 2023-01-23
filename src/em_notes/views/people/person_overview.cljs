(ns em-notes.views.people.person-overview
  (:require
   [re-frame.core :as rf]
   [em-notes.lib.local-state :refer [local-state]]
   [em-notes.components.form-footer :refer [form-footer]]
   [em-notes.i18n.tr :refer [grab]]
   [em-notes.subs :as subs]
   [em-notes.views.people.person-details :refer [person-details]]
   [em-notes.views.people.person-feedback :refer [person-feedback]]
   [em-notes.views.people.person-mood :refer [person-mood]]
   [em-notes.events.events :as events]
   [em-notes.views.people.person-support :refer [person-support]] 
   [em-notes.views.people.person-growth :refer [person-growth]]
   [em-notes.routing.nav :as nav]))


(defn reset-person! []
  (rf/dispatch [::events/reset-active-person]))

(defn person-overivew [person revise!]
  [:section
   [:div.container
    [:button {:class "button is-info mt-5" :on-click #(nav/go :home)} (str "< " (grab :home/home))]]
   [:div.container
    [:div
     [:h1 {:class "title mt-5"}
      (grab :person/title)]

     [:form
      [person-details person revise!]
      [:hr]
      [person-feedback person revise!]
      [:hr]
      [person-mood person revise!]
      [:hr]
      [person-support person revise!]
      [:hr]
      [person-growth person revise!]]

     [form-footer (fn []
                    (rf/dispatch [::events/save-person @person])
                    (reset-person!)), #(reset-person!)]]]])
