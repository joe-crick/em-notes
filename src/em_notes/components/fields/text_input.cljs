(ns em-notes.components.fields.text-input)

(defn text-input [atom revise! config]
  (let [{label :label property :property className :class} config
        class (if (nil? className) "input" (str "input " className))
        name (last property)] 
    (fn []
      [:div.field
       [:label label]
       [:p.control
        [:input {:class class
                 :html-for name
                 :type "text"
                 :id name,
                 :value (get-in @atom property)
                 :on-change #(revise! property %)}]]])))

(defn set-text-input [atom revise!]
  (partial text-input atom revise!))