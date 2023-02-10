(ns em-notes.components.tabbed-view
  (:require [em-notes.components.card :refer [card]]
            [em-notes.components.left-right-cols :refer [left-right]]
            [em-notes.events :as events]
            [em-notes.lib.current-tab :refer [current-tab?]]
            [em-notes.lib.local-state :refer [local-state]]
            [re-frame.core :as rf]))


(defn tabbed-view [views-config is-home?]
  (let [active-view (:active-view views-config)
        [tab change-tab!] (local-state (if (nil? active-view) (ffirst (:tab-navs views-config)) active-view))
        tab-navs (:tab-navs views-config)
        action-buttons (:action-buttons views-config)
        save-tab-view (if (or (nil? is-home?) (= is-home? false))
                        (fn [])
                        (fn [name] (rf/dispatch [::events/set-active-home-view name])))]
    (fn []
      [:section

     ;; show in view to make atom reactive
       [:div.is-hidden (str @tab)]

     ;; Tabs
       [:div {:class "container mb-1"}
        [left-right (fn []) (fn []
                              [:div
                               (for [[name label] tab-navs]
                                 ^{:key (random-uuid)} [:button {:class (str "button " (current-tab? @tab name))
                                                                 :data-name name
                                                                 :on-click (fn []
                                                                             (save-tab-view name)
                                                                             (change-tab! name))} label])])]]
     ;; Actions
       [:div.container
        [left-right (fn [])
         (fn []
           [:div.container
            (for [[on-click label btn-type] action-buttons]
              ^{:key (random-uuid)} [:button {:class (str "button mt-5 mb-3 " btn-type) :on-click on-click} label])])]]

     ;; Body
       [card
        (fn [] [:div
                [:h1 {:class "title"} (:title views-config)]
                [(get-in views-config [:views @tab] (fn [] [:div.container "Not Found"]))]])]])))


