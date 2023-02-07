(ns em-notes.views.people.person-profile.profile-form
  (:require
   [em-notes.components.form-footer :refer [form-footer]]
   [em-notes.events :as events]
   [em-notes.i18n.tr :refer [grab]]
   [em-notes.lib.bulma-cls :refer [bulma-cls]]
   [em-notes.subs :as subs]
   [em-notes.views.people.person-profile.person-details :refer [person-details]]
   [em-notes.views.people.person-profile.person-feedback :refer [person-feedback]]
   [em-notes.views.people.person-profile.person-growth :refer [person-growth]]
   [em-notes.views.people.person-profile.person-mood :refer [person-mood]]
   [em-notes.views.people.person-profile.person-profile :refer [person-profile]]
   [em-notes.views.people.person-profile.person-support :refer [person-support]]
   [re-frame.core :as rf]))

(defn profile []
  (let [active-person (rf/subscribe [::subs/active-person])]
    (fn []
      [:div.container
       [:h1 {:class (bulma-cls :subtitle)}
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
