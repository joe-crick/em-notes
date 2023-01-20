(ns em-notes.components.form-footer
  (:require [em-notes.routing.nav :as nav]))

(defn form-footer []
  [:div {:class "is-flex is-justify-content-space-between mt-5"}
   [:div [:button {:class "button is-primary"} "Submit"]]
   [:div [:button {:class "button is-info" :on-click #(nav/go :home)} "Cancel"]]])