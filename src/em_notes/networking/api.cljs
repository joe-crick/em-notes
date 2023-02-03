(ns em-notes.networking.api
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(defn json-parse [json]
  (let [js-obj (.parse js/JSON json)]
    (js->clj js-obj :keywordize-keys true)))

(defn get-request []
  (go (<! (http/get "http://localhost:3000/db"))))


(defn read-response [response-chan callback]
  (go (let [resp (<! response-chan)]  
        (callback (json-parse (:body resp))))))

(defn get-app-db [callback]
  (read-response (get-request) callback))

(defn save-app-db [db] 
  (http/post "http://localhost:3000/db" {:json-params (dissoc db :active-panel)}))

(defn save-person [person]
  (http/post "http://localhost:3000/person" {:json-params person}))



