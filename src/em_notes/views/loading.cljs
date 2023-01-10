(ns em-notes.views.loading   
  (:require
   [em-notes.styles :as styles]))

(defn loading-panel []
  [:div
   [:h1
    {:class (styles/level1)}
    "Loading..."]])