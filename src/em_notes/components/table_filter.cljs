(ns em-notes.components.table-filter
  (:require [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.css-cls :refer [css-cls]]))

(defn table-filter [atom revise!]
  [:div
   {:class (css-cls :columns)}
   [:div {:class (css-cls :column :is-one-third)}
    [:div.field
     [:div {:class (css-cls :control :has-icons :has-icons-right)}
      [:input {:type "search"
               :class "input"
               :placeholder (grab :table/search)
               :on-change #(revise! :filter %)
               :value (:filter @atom)}]
      [:span
       {:class (css-cls :icon :is-small :is-right)}
       [:i {:class (css-cls :fas :fa-magnifying-glass)}]]]]]])
