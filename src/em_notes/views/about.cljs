(ns em-notes.views.about
  (:require [em-notes.routing.nav :as nav]))

(defn about []
  [:div
   [:h1.title "This is the About Page."]

   [:div>button {:class "button is-link" :on-click #(nav/go :home)}
    "go to Home Page"]])
