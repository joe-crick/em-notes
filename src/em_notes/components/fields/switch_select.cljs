(ns em-notes.components.fields.switch-select
  (:require [em-notes.lib.bulma-cls :refer [bulma-cls]]
            [em-notes.lib.remove-from-right :refer [remove-from-right]]))

(defn get-option-map [list val]
  (first (filter #(= (:value %) val) list)))

(defn all-but [vec to-remove]
  (filter #(not= to-remove %) vec))

(defn switch-select [value-atom prop left-options]
  [:div
   {:class (bulma-cls :columns)}
   [:div {:class (bulma-cls :column :is-one-fifth)}
    [:select.left-select
     {:multiple true
      :size 5
      :on-change (fn [evt]
                   (swap! value-atom assoc-in prop (conj (get-in @value-atom prop) (get-option-map left-options (.. evt -target -value)))))}
     (for [{:keys [value label]} (remove-from-right left-options (get-in @value-atom prop))]
       ^{:key (random-uuid)} [:option {:value value} label])]] 
   [:div {:class (bulma-cls :column :is-one-fifth)}
    [:select.right-select
     {:multiple true
      :size 5
      :value (get-in @value-atom prop)
      :on-change (fn [evt]
                   (swap! value-atom assoc-in prop (all-but (get-in @value-atom prop) (get-option-map left-options (.. evt -target -value)))))}
     (for [{:keys [value label]} (get-in @value-atom prop)]
       ^{:key (random-uuid)} [:option {:value value} label])]]])
