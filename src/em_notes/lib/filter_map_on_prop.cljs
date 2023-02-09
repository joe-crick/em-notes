(ns em-notes.lib.filter-map-on-prop
  (:require [clojure.string :refer [includes?]]
            [em-notes.lib.lower-case :refer [lower-case]]))

(defn filter-map-on-prop [map prop search-val]
  (let [items (vals map)
        filtered-items (filter #(includes? (lower-case (get-in % prop)) (lower-case search-val))
                               items)]
    filtered-items))