(ns em-notes.routes
  (:require
   [em-notes.events :as events]
   [em-notes.views.about :as about]
   [em-notes.views.home :as home]
   [pushy.core :as pushy]
   [re-frame.core :as re-frame]))

(def routes
  (atom
   {"/"      [home/home-panel]
    "/about" [about/about-panel]}))

(defn parse
  [url]
  [url (get @routes url)])

(defn dispatch
  [route-pair]
  (let [[url route] route-pair
        key-url (if (= url "home") "/" url)]
    (re-frame/dispatch [::events/set-active-panel [(keyword key-url) route]])))

(defonce history
  (pushy/pushy dispatch parse))

(defn navigate!
  [handler]
  (let [url (if (= handler "home") "" handler)]
    (pushy/set-token! history (str "/" url)))
  )

(defn start!
  []
  (pushy/start! history))

(re-frame/reg-fx
  :navigate
  (fn [handler]
    (navigate! (.-name handler))))
