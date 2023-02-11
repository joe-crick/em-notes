(ns em-notes.views.note 
  (:require [em-notes.components.form-footer :refer [form-footer]]
            [em-notes.lib.css-cls :refer [css-cls]]))

(defn create-note []
  [:section
   [:div.container
    [:div
     [:h1 {:class (css-cls :title :mt-5)}
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
     [:div
      {:class (css-cls :select :is-multiple)}
      [:select
       {:multiple "", :size "5"}
       [:option {:value "Argentina"} "Argentina"]
       [:option {:value "Bolivia"} "Bolivia"]
       [:option {:value "Brazil"} "Brazil"]
       [:option {:value "Chile"} "Chile"]
       [:option {:value "Colombia"} "Colombia"]
       [:option {:value "Ecuador"} "Ecuador"]
       [:option {:value "Guyana"} "Guyana"]
       [:option {:value "Paraguay"} "Paraguay"]
       [:option {:value "Peru"} "Peru"]
       [:option {:value "Suriname"} "Suriname"]
       [:option {:value "Uruguay"} "Uruguay"]
       [:option {:value "Venezuela"} "Venezuela"]]]
     [form-footer]]]])