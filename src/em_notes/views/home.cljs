(ns em-notes.views.home
  (:require
   [re-frame.core :as re-frame]
   [em-notes.styles :as styles]
   [em-notes.events :as events]
   [em-notes.subs :as subs]))

(defn home-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1
      {:class (styles/level1)}
      (str "Hello from " @name ". This is the Home Page.")]

     [:div>:a {:on-click #(re-frame/dispatch [::events/navigate :about])}
       "go to About Page"]]))