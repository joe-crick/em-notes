(ns em-notes.components.left-right-cols 
  (:require [em-notes.lib.css-cls :refer [css-cls]]))

(defn left-right [left right]
  [:nav {:class (css-cls :level)}
   [:div {:class (css-cls :level-left)}
    [left]]
   [:div {:class (css-cls :level-right)}
    [right]]])