(ns em-notes.views.about
  (:require
   [re-frame.core :as re-frame]
   [em-notes.events :as events]))

(defn about-panel []
  [:div
   [:h1.title "This is the About Page."]

   [:div>button {:class "button is-link" :on-click #(re-frame/dispatch [::events/navigate :home])}
    "go to Home Page"]])
