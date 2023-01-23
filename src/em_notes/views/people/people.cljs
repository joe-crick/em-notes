(ns em-notes.views.people.people
    (:require 
     [em-notes.lib.local-state :refer [local-state]] 
     [em-notes.routing.nav :as nav]
     [em-notes.i18n.tr :refer [grab]]))



(defn people []
  ;; setup local state
  (let []
    ;; required when local state is used, because we need to return a render function
    (fn []
      [:section
       [:div.container
        [:div
         [:h1 {:class "title mt-5"}
          (grab :person/title)]

         [:div.container
          [:div>button {:class "button is-link" :on-click #(nav/go :create-person)}
           (grab :home/create-person)]]]]])))

