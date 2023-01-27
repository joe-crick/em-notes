(ns em-notes.components.modal
  (:require
   [em-notes.events :as events]
   [em-notes.components.empty :refer [no-op]]
   [re-frame.core :as rf]))

(defn modal [display title content]
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
               :on-click #(rf/dispatch [::events/set-modal {:title ""
                                                            :content no-op
                                                            :footer []
                                                            :display "is-hidden"}])}]]
    [:section {:class "modal-card-body"} (if content [content] "")]]])