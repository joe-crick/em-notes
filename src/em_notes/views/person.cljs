(ns em-notes.views.person
  (:require
   [reagent.core :as r]
   [em-notes.lib.update-atom :refer [revise!]]
   [em-notes.components.form-footer :refer [form-footer]]
   [em-notes.i18n.tr :refer [grab]]))

(defn create-person []
  ;; setup local state
  (let [data {:first-name "" :last-name "" :team ""}
        person (r/atom data)]
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
                           :on-change #(revise! person :first-name %)
                        ;;  :on-change #(println (.. % -target -value))
                           }]
            [:input {:class "input mt-5"
                     :type "text"
                     :id :last-name,
                     :placeholder (grab :person/last-name)
                     :value (:last-name @person)}]]]
          [:div.field
           [:p.control
            [:input.input {:type "text",
                           :placeholder (grab :person/team)
                           :value (:team @person)}]]]]
         [form-footer]]]])))