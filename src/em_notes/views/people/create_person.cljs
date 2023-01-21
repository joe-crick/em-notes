(ns em-notes.views.people.create-person
  (:require
   [re-frame.core :as rf]
   [em-notes.lib.get-state :refer [get-state]]
   [em-notes.components.form-footer :refer [form-footer]]
   [em-notes.db :refer [default-db]]
   [em-notes.i18n.tr :refer [grab]]
   [em-notes.views.people.person-details :refer [person-details]]
   [em-notes.views.people.person-feedback :refer [person-feedback]]
   [em-notes.views.people.person-mood :refer [person-mood]]
   [em-notes.views.people.person-support :refer [person-support]]
   [em-notes.views.people.person-growth :refer [person-growth]]))

(defn create-person []
  ;; setup local state 
  (let [[person revise!] (get-state (:person default-db))]
    ;; required when local state is used, because we need to return a render function
    (fn []
      [:section
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
         
         [form-footer #(rf/dispatch [:create-person @person])]]]])))



