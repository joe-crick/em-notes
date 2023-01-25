(ns em-notes.components.fields.textarea)

(defn text-area [atom revise! config]
  (let [{label :label property :property className :class} config
        class (if (nil? className) "input" (str "input " className))
        name (last property)]
    (fn []
      [:div.field
       [:label label]
       [:p.control
        [:textarea {:class class
                    :html-for name
                    :id name
                    :on-change #(revise! property %)} (get-in @atom property)]]])))