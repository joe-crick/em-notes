(ns em-notes.views.person
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [em-notes.routing.nav :as nav]
   [em-notes.lib.update-atom :refer [set-revise]]
   [em-notes.components.form-footer :refer [form-footer]]
   [em-notes.i18n.tr :refer [grab]]))

(defn create-person []
  ;; setup local state
  (let [data {:first-name "" :last-name "" :team ""}
        person (r/atom data)
        revise! (set-revise person)]
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



(defn tasks []
  [:div "Tasks"])

(defn performance []
  [:div "Performance"])

(defn overview []
  [:div "Overview"])

(defn active-tab [tab] 
  (case tab
    :tasks [tasks]
    :performance [performance]
    [overview])
  )


(defn people []
  ;; setup local state
  (let [data :overview
        tab (r/atom data)
        revise! (set-revise tab)]
    ;; required when local state is used, because we need to return a render function
    (fn []
      [:section
       [:div.container
        [:div
         [:h1 {:class "title mt-5"}
          (grab :person/title)]

         [:div.container
          [:button.link {:on-click #(revise! :overview)} (grab :person/overview)]
          [:button.link {:on-click #(revise! :performance)} (grab :person/performance)]
          [:button.link {:on-click #(revise! :tasks)} (grab :person/tasks)]]

         [:form
          [:div.field
           [:p.control
            [active-tab @tab]]]]

         [form-footer #(rf/dispatch [:create-person @tab])]]]])))