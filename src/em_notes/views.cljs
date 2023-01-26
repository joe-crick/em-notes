(ns em-notes.views
  (:require
   [re-frame.core :as re-frame]
   [em-notes.subs :as subs]
   [em-notes.views.loading :as loading]
   [em-notes.components.toast :refer [toast]]))

;; A simple function that subscribes to the active-panel in the store
;; Automatically updates and renders whatever view is stored in the 
;; active panel. See routes.cljs for the full routing table.
(defn main-panel []
  (let [route (re-frame/subscribe [::subs/active-panel])
        toasts (re-frame/subscribe [::subs/toasts])
        [active-panel query] @route]
    (println "active-panel: " (str active-panel))
    [:div.section
     [toast toasts]
     (or active-panel [loading/loading-splash])]))