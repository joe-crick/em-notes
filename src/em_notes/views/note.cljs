(ns em-notes.views.note 
  (:require [em-notes.routing.nav :as nav]))

(defn create-note []
  [:section
   [:div.container
    [:div
     [:h1 {:class "title mt-5"}
      "Note"]

     [:form
      [:div.field
       [:p.control
        [:input.input {:type "text", :placeholder "Name"}]]]
      [:div.field
       [:p.control
        [:input.input {:type "date", :placeholder "Date"}]]]
      [:div.field
       [:label.label "Note"]
       [:div.control
        [:textarea.textarea {:placeholder "Textarea"}]]]]
     [:div {:class "is-flex is-justify-content-space-between mt-5"}
      [:div [:button {:class "button is-primary"} "Submit"]]
      [:div [:button {:class "button is-info" :on-click #(nav/go :home)} "Cancel"]]]]]])