(ns em-notes.lib.filter-map-on-prop
  (:require [clojure.string :refer [includes?]]
            [em-notes.lib.lower-case :refer [lower-case]]))

(defn filter-map-on-prop [map prop search-val]
  (let [items (vals map)
        filtered-items (filter #(includes? (lower-case (get-in % prop)) (lower-case search-val))
                               items)]
    filtered-items))

(defn filter-on-prop-str [items prop search-val]
  (if (or (= "" search-val) (nil? search-val))
    items
    (filter #(includes?
              (lower-case (get-in % prop)) (lower-case search-val))
            items)))

(defn filter-on-prop [items prop search-val]
  (filter #(= (get-in % prop) search-val) items))

(defn filter-not-on-prop [items prop search-val]
  (filter #(not (includes?
                 (lower-case (get-in % prop)) (lower-case search-val)))
          items))

(defn eq-prop [item prop search-val]
  (includes? (lower-case (get-in item prop)) (lower-case search-val)))

(defn replace-map-by-prop [items prop comparison-map]
  (map (fn [map]
         (if (= (prop map) (prop comparison-map))
           comparison-map
           map))
       items))
