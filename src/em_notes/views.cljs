(ns em-notes.views
  (:require
   [re-frame.core :as re-frame]
   [em-notes.subs :as subs]
   [em-notes.views.home :as home]
   [em-notes.views.about :as about]
   ))




;; main
(defn page [page-name]
  (js/console.log "page-name:" page-name)
  (case page-name
    :home-panel [home/home-panel]
    :about-panel [about/about-panel]
    [home/home-panel]))
  

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    (page @active-panel)))
