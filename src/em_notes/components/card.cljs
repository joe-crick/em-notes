(ns em-notes.components.card 
  (:require [em-notes.lib.css-cls :refer [css-cls]]))

(defn card [content]
  [:div {:class (css-cls :container)}
   [:div.card
    [:div {:class (css-cls :card-container) :style {:padding "15px 10px 15px 15px"}}
     [content]]]])