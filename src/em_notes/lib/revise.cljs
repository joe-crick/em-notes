(ns em-notes.lib.revise
  (:require 
   [em-notes.lib.get-evt-val :refer [get-evt-val]]
   [em-notes.events :as events]
    [re-frame.core :as rf]))

(defn revise!
  ;; replace a value
  ([atom val]
   (reset! atom val))
  ;; update a value in a map/object
  ([atom field evt]
   (let [prop (if (vector? field) field [field])]
     (swap! atom assoc-in prop (.. evt -target -value)))))

(defn set-revise [atom]
  (partial revise! atom))

(defn get-revise! []
  (fn [property evt]
    (let [value (get-evt-val evt)]
      (rf/dispatch [::events/set-person-field [property value]])))
  )