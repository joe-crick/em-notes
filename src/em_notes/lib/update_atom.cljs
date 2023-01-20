(ns em-notes.lib.update-atom)

(defn revise! [atom field evt] 
  (swap! atom assoc field (.. evt -target -value))
  (println @atom))

(defn set-update [atom]
  (partial revise! atom))