(ns em-notes.components.section-toggle
  (:require [em-notes.lib.local-state :refer [local-state]]))

(defn toggle [component content display? display-type]
  (let [[show? revise!] (local-state (if (nil? display?) false display?))
        display (if (nil? display-type) "is-block" display-type)]
    (fn []
      [:div.container
       [:button {:class (str "button " (if @show? "is-primary is-light" "")) :on-click #(revise! (not @show?)) :type "button"} content]
       [:div {:class (if @show? display "is-hidden")}
        [component]]])))

(defn section-toggle
  ([component content] (toggle component content nil nil))
  ([component content display?] (toggle component content display? nil))
  ([component content display? display-type] (toggle component content display? display-type))
  )