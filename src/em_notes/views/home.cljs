(ns em-notes.views.home
  (:require [em-notes.routing.nav :as nav]
            [em-notes.subs :as subs]
            [re-frame.core :as re-frame]))

(defn home-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1.title
      (str "Hello from " @name ". This is the Home Page.")]

     [:div>a {:on-click #(nav/go :note)}
       "Create a Note"]]))