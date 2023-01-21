(ns em-notes.components.section-toggle
  (:require [em-notes.lib.get-state :refer [get-state]]))

(defn section-toggle [component & display-type]
  (let [[show? revise!] (get-state true)
        display (if (nil? display-type) "is-block" display-type)]
    (fn []
      [:div.container
       [:button.button {:on-click #(revise! (not @show?)) :type :button}]
       [:div {:class (if @show? display "is-hidden")}
        [component]]])))