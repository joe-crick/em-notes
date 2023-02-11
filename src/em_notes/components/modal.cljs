(ns em-notes.components.modal
  (:require [em-notes.db :refer [default-db]]
            [em-notes.events :as events]
            [em-notes.lib.css-cls :refer [css-cls]]
            [re-frame.core :as rf]))

(defn modal [display title content on-close]
  [:div
   {:class (str "modal " display)}
   [:div {:class (css-cls :modal-background)}]
   [:div
    {:class (css-cls :modal-card)}
    [:header
     {:class (css-cls :modal-card-head)}
     [:p {:class (css-cls :modal-card-title)} title]
     [:button {:class (css-cls :delete)
               :aria-label "close"
               :on-click (if (nil? on-close) #(rf/dispatch [::events/set-modal (:default-modal default-db)]) on-close)}]]
    [:section {:class (css-cls :modal-card-body)} (if content [content] "")]]])