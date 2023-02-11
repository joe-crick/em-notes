(ns em-notes.components.toast 
  (:require [em-notes.events :as events]
            [em-notes.lib.css-cls :refer [css-cls]]
            [re-frame.core :as re-frame]))

(defn toast [toasts] 
  [:div {:class (css-cls :columns :is-flex :is-justify-content-flex-end :mt-1 :mr-5 :is-overlay)}
   [:div {:class (css-cls :column :has-text-centered :is-one-fifth)}
    (for [[msg type] @toasts
          :let [className (str "notification " type)]]
      ^{:key (random-uuid)} [:div {:class className :style {:z-index "100" :position "fixed"}}
                   [:button {:class (css-cls :delete) :on-click #(re-frame/dispatch-sync [::events/clear-toasts])}]
                   msg])
    ]])