(ns em-notes.components.toast 
  (:require [re-frame.core :as re-frame]
            [em-notes.events :as events]))

(defn toast [toasts]
  [:div {:class "columns is-flex is-justify-content-flex-end mt-1 mr-5 is-overlay"}
   [:div {:class "column has-text-centered is-one-fifth"}
    (for [[msg type] @toasts
          :let [className (str "notification " type)]]
      ^{:key msg} [:div {:class className :style {:z-index "100"}}
                     [:button {:class "delete" :on-click #(re-frame/dispatch-sync [::events/clear-toasts])}]
                     msg])]])