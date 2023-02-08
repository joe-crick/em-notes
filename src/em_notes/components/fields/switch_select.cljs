(ns em-notes.components.fields.switch-select
  (:require [em-notes.lib.bulma-cls :refer [bulma-cls]]
            [em-notes.lib.remove-from-right :refer [remove-from-right]]))

(defn get-option-map [list val]
  (first (filter #(= (:value %) val) list)))

(defn all-but [vec to-remove]
  (filter #(not= to-remove %) vec))

(defn switch-select [value-atom left-options]
  (prn "vals" (:values @value-atom))
  (prn "left-opts" (remove-from-right left-options (:values @value-atom)))
  [:div
   {:class (bulma-cls :columns)}
   [:div {:class (bulma-cls :column :is-one-fifth)}
    [:select.left-select
     {:multiple true
      :size 5
      :on-change (fn [evt]
                   (swap! value-atom assoc-in [:values] (conj (:values @value-atom) (get-option-map left-options (.. evt -target -value)))))}
     (for [{:keys [value label]} (remove-from-right left-options (:values @value-atom))]
       ^{:key (random-uuid)} [:option {:value value} label])]] 
   [:div {:class (bulma-cls :column :is-one-fifth)}
    [:select.right-select
     {:multiple true
      :size 5
      :value (get-in @value-atom [:values])
      :on-change (fn [evt]
                   (swap! value-atom assoc-in [:values] (all-but (:values @value-atom) (get-option-map left-options (.. evt -target -value)))))}
     (for [{:keys [value label]} (:values @value-atom)]
       ^{:key (random-uuid)} [:option {:value value} label])]]])
