(ns em-notes.components.left-right-cols)

(defn left-right [left right]
  [:nav {:class "level"}
   [:div {:class "level-left"}
    [left]]
   [:div {:class "level-right"}
    [right]]])