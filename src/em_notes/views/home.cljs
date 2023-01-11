(ns em-notes.views.home
  (:require [em-notes.routing.nav :as nav]))

(defn home []
  [:section.mt-5
   [:div.container
    [:h1.title
     (str "EM Notes")]

    [:div>button {:class "button is-link" :on-click #(nav/go :note)}
     "Create a Note"]]])