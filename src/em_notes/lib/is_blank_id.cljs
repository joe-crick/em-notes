(ns em-notes.lib.is-blank-id)

(defn is-blank-id [id obj]
  (or (nil? (id obj)) (= (id obj) "")))