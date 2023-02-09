(ns em-notes.components.table-filter
  (:require [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.bulma-cls :refer [bulma-cls]]))

(defn table-filter [atom revise!]
  [:div
   {:class (bulma-cls :columns)}
   [:div {:class (bulma-cls :column :is-one-third)}
    [:div.field
     [:div {:class (bulma-cls :control :has-icons :has-icons-right)}
      [:input {:type "search"
               :class "input"
               :placeholder (grab :table/search)
               :on-change #(revise! :filter %)
               :value (:filter @atom)}]
      [:span
       {:class (bulma-cls :icon :is-small :is-right)}
       [:i {:class (bulma-cls :fas :fa-magnifying-glass)}]]]]]])
