(ns em-notes.views
  (:require
   [re-frame.core :as re-frame]
   [em-notes.subs :as subs]
   [em-notes.views.loading :as loading]
   [em-notes.components.toast :refer [toast]]
   [em-notes.components.modal :refer [modal]]
   [em-notes.components.confirmation :refer [confirm]]))

;; See routes.cljs for the full routing table.
(defn main-panel []
  (let [route (re-frame/subscribe [::subs/active-panel])
        toasts (re-frame/subscribe [::subs/toasts])
        modal-config (re-frame/subscribe [::subs/modal])
        confirm-config (re-frame/subscribe [::subs/confirm])
        {m-content :content m-title :title m-display :display} @modal-config
        {msg :msg on-confirm :on-confirm c-display :display} @confirm-config
        [active-panel query] @route]
    [:div {:class "is-flex is-flex-direction-column "}
     [:section.hero-bg
      [:div {:class "container"} [:img {:src "/img/em-notes-transp.png"}]]]
     [:section {:style {:margin-top "35px"}}
      [toast toasts]
      [confirm c-display msg on-confirm]
      [modal m-display m-title m-content]
      (if (nil? active-panel)
        [loading/loading-splash]
        [active-panel query])]]))