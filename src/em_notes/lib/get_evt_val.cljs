(ns em-notes.lib.get-evt-val)

(defn get-evt-val [evt]
  (.. evt -target -value))