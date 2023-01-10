(ns em-notes.views.note 
  (:require [em-notes.routing.nav :as nav]))

(defn note-panel []
  [:section
   [:div.container
    [:div
     [:h1.title
      "Note"]

     [:form
      [:div.field
       [:label.label "Message"]
       [:div.control
        [:textarea.textarea {:placeholder "Textarea"}]]]]
     [:div {:class "is-flex is-justify-content-space-between mt-5"}
      [:div [:button {:class "button is-primary"} "Submit"]]
      [:div [:button {:class "button is-info" :on-click #(nav/go :home)} "Cancel"]]]]]])