(ns em-notes.components.modal
  (:require
   [em-notes.events :as events]
   [em-notes.db :refer [default-db]]
   [re-frame.core :as rf]))

(defn modal [display title content on-close]
  [:div
   {:class (str "modal " display)}
   [:div {:class "modal-background"}]
   [:div
    {:class "modal-card"}
    [:header
     {:class "modal-card-head"}
     [:p {:class "modal-card-title"} title]
     [:button {:class "delete"
               :aria-label "close"
               :on-click (if (nil? on-close) #(rf/dispatch [::events/set-modal (:default-modal default-db)]) on-close)}]]
    [:section {:class "modal-card-body"} (if content [content] "")]]])