(ns em-notes.lib.update-atom)

(defn revise! [atom field evt] 
  (swap! atom assoc field (.. evt -target -value))
  (println @atom))

(defn set-revise [atom]
  (partial revise! atom))