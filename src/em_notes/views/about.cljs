(ns em-notes.views.about
  (:require
   [re-frame.core :as re-frame]
   [em-notes.events :as events]))

;; about


(defn about-panel []
  [:div
   [:h1 "This is the About Page."]

   [:div
    [:a {:on-click #(re-frame/dispatch [::events/navigate :home])}
     "go to Home Page"]]])
