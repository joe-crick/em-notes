(ns em-notes.views.people.create-person
  (:require
   [re-frame.core :as rf]
   [em-notes.lib.get-state :refer [get-state]]
   [em-notes.components.form-footer :refer [form-footer]]
   [em-notes.db :refer [default-db]]
   [em-notes.i18n.tr :refer [grab]]))

(defn create-person []
  ;; setup local state
  
  (let [[person revise!] (get-state (:person default-db))]
    (println person)
    ;; required when local state is used, because we need to return a render function
    (fn []
      [:section
       [:div.container
        [:div
         [:h1 {:class "title mt-5"}
          (grab :person/title)]

         [:form
          [:div.field
           [:p.control
            [:input.input {:html-for :first-name
                           :type "text"
                           :id :first-name,
                           :placeholder (grab :person/first-name)
                           :value (:first-name @person)
                           :on-change #(revise! :first-name %)}]
            [:input {:class "input mt-5"
                     :type "text"
                     :id :last-name,
                     :placeholder (grab :person/last-name)
                     :value (:last-name @person)
                     :on-change #(revise! :last-name %)}]]]
          [:div.field
           [:p.control
            [:input.input {:type "text",
                           :placeholder (grab :person/team)
                           :value (:team @person)
                           :on-change #(revise! :team %)}]]]]
         [form-footer #(rf/dispatch [:create-person @person])]]]])))



