(ns em-notes.routing.routing
  (:require
   [em-notes.events :as events]
   [em-notes.routing.routes :refer [routes]]
   [pushy.core :as pushy]
   [re-frame.core :as re-frame]))

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
    (pushy/set-token! history (str "/" url))))

(re-frame/reg-fx
 :navigate
 (fn [handler]
   (navigate! (.-name handler))))

(pushy/start! history)
