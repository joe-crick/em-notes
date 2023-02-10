(ns em-notes.lib.current-tab)

(defn current-tab? [tab cur-tab]
  (if (= cur-tab tab) "is-info" ""))