(ns em-notes.lib.update-atom)

(defn revise!
  ;; replace a value
  ([atom val] 
   (reset! atom val))
  ;; update a value in a map/object
  ([atom field evt] 
   (swap! atom assoc field (.. evt -target -value))))

(defn set-revise [atom]
  (partial revise! atom))