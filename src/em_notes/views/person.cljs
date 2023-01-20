(ns em-notes.views.person
  (:require [em-notes.components.form-footer :refer [form-footer]]))

(defn create-person []
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
        [:input.input {:type "text", :placeholder "Department"}]]]
      [:div.field
       [:label.label "Note"]
       [:div.control
        [:textarea.textarea {:placeholder "Textarea"}]]]]
     [form-footer]
     ]]])