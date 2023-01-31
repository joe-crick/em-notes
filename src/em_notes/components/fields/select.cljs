(ns em-notes.components.fields.select)

(defn select [atom revise! config]
  (let [{label :label property :property className :class values :values default-value :default-value} config
        name (last property)
        value (get-in @atom property)]
    (fn []
      [:div.field
       [:label label]
       [:div.control
        [:div.select
         [:select {:value (if (nil? value) default-value value)
                   :class className
                   :id name
                   :html-for name
                   :on-change #(revise! property %)}
          (for [[val text] values]
            ^{:key (random-uuid)} [:option {:value val} text])]]]])))

(defn set-select [atom revise!]
  (partial select atom revise!))