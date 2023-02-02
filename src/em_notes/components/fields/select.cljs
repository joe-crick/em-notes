(ns em-notes.components.fields.select 
  (:require [em-notes.lib.includes :refer [includes?]]))


(defn get-multi-val [atom val]
  (if (includes? atom val) (remove #(= % val) atom) (conj atom val)))

(defn select [atom revise! config]
  (let [{label :label property :property className :class values :values default-value :default-value multi? :multi?} config
        name (last property)]
    (prn "atom" @atom)
    (fn []
      [:div.field
       [:label label]
       [:div.control
        [:div {:class (str "select" (if multi? " is-multiple" ""))}
         [:select {:value (if (nil? (get-in @atom property)) default-value (get-in @atom property))
                   :class className
                   :id name
                   :html-for name
                   :multiple multi?
                   :size (if multi? "5" "1")
                   :on-change (fn [evt]
                                (let [new-val (.. evt -target -value)]
                                  (prn "new val" (swap! atom assoc-in property (if multi? (get-multi-val (get-in @atom property) new-val) new-val)))
                                  (swap! atom assoc-in property (if multi? (get-multi-val (get-in @atom property) new-val) new-val))))}
          (for [[v text] values
                :let [val (if (nil? v) text v)]]
            (do
              ^{:key (random-uuid)} [:option {:value val} text]))]]]])))

(defn set-select [atom revise!]
  (partial select atom revise!))