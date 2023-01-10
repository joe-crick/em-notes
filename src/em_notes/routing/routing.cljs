(ns em-notes.routing.routing
  (:require
   [em-notes.events :as events]
   [em-notes.routing.routes :refer [routes]]
   [pushy.core :as pushy]
   [re-frame.core :as re-frame]))

(defn parse
  [url]
  (js/console.log "parse:")
  [url (get @routes url)])

(defn dispatch
  [route-pair]
  (let [[url route] route-pair
        key-url (if (= url "home") "/" url)]
    (js/console.log "dispatch:")
    (re-frame/dispatch [::events/set-active-panel [(keyword key-url) route]])))

(defonce history
  (pushy/pushy dispatch parse))

(defn navigate!
  [handler]
  (let [url (if (= handler "home") "" handler)]
    (js/console.log "navigate!:" url)
    (pushy/set-token! history (str "/" url))))

(re-frame/reg-fx
 :navigate
 (fn [handler]
   (navigate! (.-name handler))))

;; called by init
(defn start!
  []
  (pushy/start! history))
