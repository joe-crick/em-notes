(ns em-notes.routing.nav 
  (:require
   [re-frame.core :as re-frame]
   [em-notes.events :as events]))

(defn go [route]
  (re-frame/dispatch [::events/navigate route]))