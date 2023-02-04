(ns em-notes.views.people.person-profile.profile-form
  (:require
   [re-frame.core :as rf]
   [em-notes.components.form-footer :refer [form-footer]]
   [em-notes.views.people.person-profile.person-details :refer [person-details]]
   [em-notes.views.people.person-profile.person-feedback :refer [person-feedback]]
   [em-notes.views.people.person-profile.person-mood :refer [person-mood]]
   [em-notes.i18n.tr :refer [grab]]
   [em-notes.events :as events]
   [em-notes.subs :as subs]
   [em-notes.views.people.person-profile.person-support :refer [person-support]]
   [em-notes.views.people.person-profile.person-growth :refer [person-growth]]
   [em-notes.views.people.person-profile.person-profile :refer [person-profile]]))

(defn profile []
  (let [active-person (rf/subscribe [::subs/active-person])]
    (fn []
      [:div.container
       [:h1 {:class "subtitle"}
        (grab :person/profile)]
       [:div
        [:form
         [person-details]
         [:hr]
         [person-feedback]
         [:hr]
         [person-mood]
         [:hr]
         [person-support]
         [:hr]
         [person-growth]
         [:hr]
         [person-profile]]

        [form-footer #(rf/dispatch [::events/save-person @active-person]),
         #(rf/dispatch-sync [::events/reset-active-person])]]])))
