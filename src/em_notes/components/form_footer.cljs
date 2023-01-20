(ns em-notes.components.form-footer
  (:require [em-notes.routing.nav :as nav]
            [em-notes.i18n.tr :refer [grab]]))

(defn form-footer [submit]
  [:div {:class "is-flex is-justify-content-space-between mt-5"}
   [:div [:button {:class "button is-primary" :on-click submit} (grab :form/submit)]]
   [:div [:button {:class "button is-info" :on-click #(nav/go :home)} (grab :form/cancel)]]])