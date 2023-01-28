(ns em-notes.components.card)

(defn card [content]
  [:div {:class "container"}
   [:div.card
    [:div {:class "card-container" :style {:padding "15px 10px 15px 15px"}}
     [content]]]])