(ns em-notes.networking.api
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(defn json-parse [json]
  (let [js-obj (.parse js/JSON json)]
    (js->clj js-obj :keywordize-keys true)))


(defn read-response [response-chan callback]
  (go (let [resp (<! response-chan)]  
        (callback (json-parse (:body resp))))))

(def api-url "http://localhost:3000/")

;; APP DB

(defn _get-app-db []
  (go (<! (http/get (str api-url "db")))))

(defn get-app-db [callback]
  (read-response (_get-app-db) callback))

(defn save-app-db [db]
  (http/post (str api-url "db") {:json-params (dissoc db :active-panel)}))

;; PERSON

(defn _get-person [person-id]
  (go (<! (http/get (str api-url "person?id=" person-id)))))

(defn get-person [person-id callback]
  (read-response (_get-person person-id) callback))

(defn save-person [person]
  (http/post (str api-url "person") {:json-params person}))

(defn del-person [person]
  (http/delete (str api-url "person") {:json-params person}))

;; TASKS

(defn _get-tasks []
  (go (<! (http/get (str api-url "tasks")))))

(defn get-tasks [callback]
  (read-response (_get-tasks) callback))







