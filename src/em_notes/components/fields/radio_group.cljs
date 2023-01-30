(ns em-notes.components.fields.radio-group)

(defn radio-group [atom config transform]
  (let [{label :label property :property className :class values :values} config
        name (last property)
        trans (if (nil? transform) identity transform)]
    (fn []
      [:div.field
       [:label label]
       [:div.is-hidden (get-in @atom property)]
       [:p.control
        (for [[val text] values]
          ^{:key (random-uuid)} [:label.radio
                                 [:input {:type "radio"
                                          :value val
                                          :name name
                                          :checked (= val (get-in @atom property))
                                          :on-change (fn [evt]
                                                       (let [new-val (trans (.. evt -target -value))]
                                                         (swap! atom assoc-in property new-val)))
                                          :class className}] (str " " text)])]])))