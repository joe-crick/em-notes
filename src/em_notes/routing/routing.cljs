(ns em-notes.routing.routing
  (:require
   [em-notes.events :as events]
   [em-notes.routing.routes :refer [routes]]
   [pushy.core :as pushy]
   [lambdaisland.uri :as uri]
   [re-frame.core :as re-frame]))

(defn parse
  [url] 
  [url (let [route (:path (uri/uri url))]
         (get @routes route))])

(defn dispatch
  [route-pair]
  (let [[url route] route-pair 
        query (:query (uri/uri url))]
    (re-frame/dispatch [::events/set-active-panel [route query]])))

(defonce history
  (pushy/pushy dispatch parse))

(defn navigate!
  [handler]
  (let [url (if (= handler "home") "" handler)]
    (pushy/set-token! history (str "/" url))))

(re-frame/reg-fx
 :navigate
 (fn [handler]
   (navigate! (.-name handler))))

;; called by init
(defn start!
  []
  (pushy/start! history))
