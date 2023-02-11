(ns em-notes.lib.filter-map-on-prop
  (:require [clojure.string :refer [includes?]]
            [em-notes.lib.lower-case :refer [lower-case]]))

(defn filter-map-on-prop [map prop search-val]
  "Removes any item from a set where there is NOT a partial match on the set based on the search value string"
  (let [items (vals map)
        filtered-items (filter #(includes? (lower-case (get-in % prop)) (lower-case search-val))
                               items)]
    filtered-items))

(defn filter-on-prop-str [items prop search-val]
  "Removes any item from a set where there is NOT a partial match on the set based on the search value string"
  (if (or (= "" search-val) (nil? search-val))
    items
    (filter #(includes?
              (lower-case (get-in % prop)) (lower-case search-val))
            items)))

(defn filter-on-prop [items prop search-val]
  "Removes any item from a set where there is NOT a partial match on the set based on the search value - direct value comparison"
  (filter #(= (get-in % prop) search-val) items))

(defn filter-not-on-prop [items prop search-val]
  "Removes any item from a set where there is a partial match on the set based on the search value - direct value comparison"
  (filter #(not (includes?
                 (lower-case (get-in % prop)) (lower-case search-val)))
          items))

(defn replace-map-by-prop [items prop comparison-map]
  "Deprecated - Use update-map-in-vector"
  (map (fn [map]
         (if (= (prop map) (prop comparison-map))
           comparison-map
           map))
       items))


(defn find-index [id-prop id vector-of-maps]
  "Finds the index of a map in a vector based on a shared key"
  (reduce (fn [result map]
            (if (= (id-prop map) id)
              (if (nil? result)
                (count vector-of-maps)
                result)
              (dec result)))
          (count vector-of-maps)
          (reverse vector-of-maps)))



(defn update-map-in-vector [vec map key-property]
  "Will either update or add a map to a vector of maps, when provided a shared key with which to identify new or existing maps"
  (let [existing-map (first (filter #(= (map key-property) (% key-property)) vec))
        index (if existing-map (find-index key-property vec ((keyword key-property) map)) nil)]
    (if existing-map
      (assoc vec index map)
      (conj vec map))))





