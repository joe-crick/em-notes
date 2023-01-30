(ns em-notes.components.fields.select)

(defn select [atom revise! config]
  (let [{label :label property :property className :class values :values} config
        name (last property)]
    (fn []
      [:div.field
       [:label label]
       [:p.control
        [:div.select
         [:select {:value (get-in @atom property)
                   :class className
                   :id name
                   :html-for name
                   :on-change #(revise! property %)}
          (for [[val text] values]
            ^{:key (random-uuid)} [:option {:value val} text])]]]])))

(defn set-select [atom revise!]
  (partial select atom revise!))