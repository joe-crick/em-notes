(ns em-notes.views.people.person-profile
  (:require
   [re-frame.core :as rf]
   [em-notes.lib.local-state :refer [local-state]]
   [em-notes.components.form-footer :refer [form-footer]]
   [em-notes.views.people.person-details :refer [person-details]]
   [em-notes.views.people.person-feedback :refer [person-feedback]]
   [em-notes.views.people.person-mood :refer [person-mood]]
   [em-notes.events :as events]
   [em-notes.subs :as subs]
   [em-notes.views.people.person-support :refer [person-support]]
   [em-notes.views.people.person-growth :refer [person-growth]]))

(defn person-profile []
  (let [active-person (rf/subscribe [::subs/active-person])
        [person revise!] (local-state @active-person)]
    (fn []
      [:div.container
       [:div
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

        [form-footer #(rf/dispatch [::events/save-person @person]),
         #(rf/dispatch-sync [::events/reset-active-person])]]])))
