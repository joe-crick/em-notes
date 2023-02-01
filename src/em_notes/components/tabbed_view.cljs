(ns em-notes.components.tabbed-view
  (:require [em-notes.components.card :refer [card]]
            [em-notes.components.left-right-cols :refer [left-right]]
            [em-notes.lib.local-state :refer [local-state]]
            [reagent.core :as r]))


(defn current-tab? [tab cur-tab]
  (if (= cur-tab tab) "is-info" "")) 

(defn tabbed-view [views]
  (let [[tab change-tab!] (local-state (ffirst (:tab-navs views)))
        tab-navs (:tab-navs views)
        action-buttons (:action-buttons views)]
    (fn []
      [:section

     ;; show in view to make atom reactive
       [:div.is-hidden (str @tab)]

     ;; Tabs
       [:div {:class "container mb-1"}
        [left-right (fn []) (fn [] [:div
                                    (for [[name label] tab-navs]
                                      ^{:key (random-uuid)} [:button {:class (str "button " (current-tab? @tab name))
                                                                      :data-name name
                                                                      :on-click #(change-tab! name)} label])])]]
     ;; Actions
       [:div.container
        [left-right (fn [])
         (fn [] [:div.container
                 (for [[on-click label btn-type] action-buttons]
                   ^{:key (random-uuid)} [:button {:class (str "button mt-5 mb-3 " btn-type) :on-click on-click} label])])]]

     ;; Body
       [card
        (fn [] [:div
                [:h1 {:class "title"} (:title views)]
                [(get-in views [:views @tab] (fn [] [:div.container "Not Found"]))]])]])))


