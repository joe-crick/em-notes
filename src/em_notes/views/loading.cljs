(ns em-notes.views.loading 
  (:require [em-notes.lib.css-cls :refer [css-cls]]))

(defn loading-splash []
  [:div.container
   [:h1 {:class (css-cls :title :is-1)} "Loading..."]
   [:progress
    {:class (css-cls :progress :is-success), :value "90", :max "100"}
    "60%"]])