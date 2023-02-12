(ns em-notes.components.fields.switch-select
  (:require [em-notes.lib.css-cls :refer [css-cls]]
            [em-notes.lib.remove-from-right :refer [remove-from-right]]))

(defn get-option-map [list val]
  (first (filter #(= (:value %) val) list)))

(defn all-but [vec to-remove]
  (filter #(not= to-remove %) vec))

(defn switch-select [value-atom prop left-options left-label right-label]
  (prn @value-atom)
  (prn prop)
  (prn (get-in @value-atom prop))
  [:div {:class (css-cls :columns)}
   [:div {:class (css-cls :column :is-one-fifth)}
    [:div.field
     [:label left-label]
     [:div.control
      [:select.left-select
       {:multiple true
        :size 5
        :on-change (fn [evt]
                     (swap! value-atom assoc-in prop (conj (get-in @value-atom prop) (get-option-map left-options (.. evt -target -value)))))}
       (for [{:keys [value label]} (remove-from-right left-options (get-in @value-atom prop))]
         ^{:key (random-uuid)} [:option {:value value} label])]]]]
   [:div {:class (css-cls :column :is-one-fifth)}
    [:div.field
     [:label right-label]
     [:div.control
      [:select.right-select
       {:multiple true
        :size 5
        :value (get-in @value-atom prop)
        :on-change (fn [evt]
                     (swap! value-atom assoc-in prop (all-but (get-in @value-atom prop) (get-option-map left-options (.. evt -target -value)))))}
       (for [{:keys [value label]} (get-in @value-atom prop)]
         ^{:key (random-uuid)} [:option {:value value} label])]]]]])
