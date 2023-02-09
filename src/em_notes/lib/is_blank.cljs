(ns em-notes.lib.is-blank)

(defn is-blank? [prop obj]
  (or (nil? (prop obj)) (= (prop obj) "")))