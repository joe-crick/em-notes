(ns em-notes.lib.nab
  (:require [applied-science.js-interop :as j]))

(defn nab [prop obj]
  (j/get obj prop)
  )