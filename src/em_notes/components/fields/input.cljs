(ns em-notes.components.fields.input)

(defn input [atom revise! config type]
  (let [{label :label property :property className :class} config
        class (if (nil? className) "input" (str "input " className))
        name (last property)]
    (fn []
      [:div.field
       [:label label]
       [:p.control
        [:input {:class class
                 :html-for name
                 :type type
                 :id name,
                 :value (get-in @atom property)
                 :on-change #(revise! property %)}]]])))