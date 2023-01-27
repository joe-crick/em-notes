(ns em-notes.views
  (:require
   [re-frame.core :as re-frame]
   [em-notes.subs :as subs]
   [em-notes.views.loading :as loading]
   [em-notes.components.toast :refer [toast]]
   [em-notes.components.modal :refer [modal]]))

;; A simple function that subscribes to the active-panel in the store
;; Automatically updates and renders whatever view is stored in the 
;; active panel. See routes.cljs for the full routing table.
(defn main-panel []
  (let [route (re-frame/subscribe [::subs/active-panel])
        toasts (re-frame/subscribe [::subs/toasts])
        modal-config (re-frame/subscribe [::subs/modal])
        {content :content title :title show? :show?} @modal-config
        modalClassName (if (= show? true) "is-block" "is-hidden")
        [active-panel query] @route]
    [:div.section
     [toast toasts]
     [modal modalClassName title content]
     (if (nil? active-panel)
       [loading/loading-splash]
       [active-panel query])]))

;;   (if footer [footer] "")