(ns em-notes.lib.includes)

(defn includes? [vec val]
  (some #(= val %) vec))