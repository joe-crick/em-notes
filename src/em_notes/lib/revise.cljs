(ns em-notes.lib.revise)

(defn revise!
  ;; replace a value
  ([atom val]
   (reset! atom val))
  ;; update a value in a map/object
  ([atom field evt]
   (let [prop (if (vector? field) field [field])]
     (swap! atom assoc-in prop (.. evt -target -value)))))

(defn set-revise [atom]
  (partial revise! atom))