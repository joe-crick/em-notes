(ns em-notes.views
  (:require
   [re-frame.core :as re-frame]
   [em-notes.subs :as subs]
   [em-notes.views.home :as home]))

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    (if-not (nil? @active-panel) (nth @active-panel 1) [home/home-panel])))
