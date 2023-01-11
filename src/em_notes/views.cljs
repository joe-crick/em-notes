(ns em-notes.views
  (:require
   [re-frame.core :as re-frame]
   [em-notes.subs :as subs]
   [em-notes.views.loading :as loading]))

;; A simple function that subscribes to the active-panel in the store
;; Automatically updates and renders whatever view is stored in the 
;; active panel. See routes.cljs for the full routing table.
(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    (nth @active-panel 1 [loading/loading-splash])))
