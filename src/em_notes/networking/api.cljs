(ns em-notes.networking.api
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [clojure.string :as str]))

(defn json-parse [json]
  (when-not (str/blank? json)
    (let [js-obj (.parse js/JSON json)]
      (js->clj js-obj :keywordize-keys true))))


(defn read-response [response-chan callback]
  (go (let [{:keys [body success status] :as resp} (<! response-chan)]
        (cond
          (not success)
          (.error js/console "API request failed" (clj->js (select-keys resp [:status :error-code :error-text :body])))

          (str/blank? body)
          (.error js/console "API response had no JSON body" (clj->js {:status status}))

          :else
          (try
            (callback (json-parse body))
            (catch js/Error e
              (.error js/console "API response was not valid JSON" e body)))))))

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






