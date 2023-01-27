(ns em-notes.routing.routing
  (:require
   [em-notes.events :as events]
   [em-notes.routing.routes :refer [routes]]
   [pushy.core :as pushy]
   [lambdaisland.uri :as uri]
   [re-frame.core :as re-frame]))

;; The default view is set in events.cljs. See ::set-init-db.

(defn parse
  [url] 
  [url (let [route (:path (uri/uri url))]
         (get @routes route))])

(defn dispatch
  [url-route-pair]
  (let [[url [route handler]] url-route-pair
        query (uri/query-map (uri/uri url))] 
    (handler query)
    (re-frame/dispatch [::events/set-active-panel [route query]])))

(defonce history
  (pushy/pushy dispatch parse))

(defn navigate!
  [handler params]
  (let [base-url (if (= handler "home") "" handler)
        url (if (nil? params) base-url (str base-url "?" params))]
    (pushy/set-token! history (str "/" url))))

(re-frame/reg-fx
 :navigate
 (fn [[handler params]] 
   (navigate! (.-name handler) params)))

;; called by init
(defn start!
  []
  (pushy/start! history))
