(ns em-notes.views.about
  (:require [em-notes.lib.css-cls :refer [css-cls]]
            [em-notes.routing.nav :as nav]))

(defn about []
  [:div
   [:h1.title "This is the About Page."]

   [:div>button {:class (css-cls :button :is-link) :on-click #(nav/go :home)}
    "go to Home Page"]])
