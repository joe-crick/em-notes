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
        {content :content title :title display :display} @modal-config
        [active-panel query] @route]
    [:div {:class "is-flex is-flex-direction-column "}
     [:section.hero-bg
      [:div {:class "container"} [:img {:src "/img/em-notes-transp.png" }]]]
     [:section {:style {:margin-top "35px"}}
      [toast toasts]
      [modal display title content]
      (if (nil? active-panel)
        [loading/loading-splash]
        [active-panel query])]]))