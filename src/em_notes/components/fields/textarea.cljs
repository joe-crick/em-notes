(ns em-notes.components.fields.textarea)

(defn text-area [atom revise! config]
  (let [{label :label property :property className :class _size :size} config
        size (if (nil? _size) "is-medium" _size)
        class (if (nil? className) (str "textarea " size) (str "textarea " size " " className))
        name (last property)]
    (fn []
      [:div.field
       [:label label]
       [:p.control
        [:textarea {:class class
                    :html-for name
                    :id name
                    :value (get-in @atom property)
                    :on-change #(revise! property %)}]]])))

(defn set-text-area [atom revise!]
  (partial text-area atom revise!))