(ns em-notes.lib.dissoc-in)

(defn dissoc-in [obj path prop]
  (update-in obj path dissoc prop))