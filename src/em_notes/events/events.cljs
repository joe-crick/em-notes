(ns em-notes.events.events
  (:require
   [re-frame.core :as re-frame]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [em-notes.lib.lower-case :refer [lower-case]]
   [em-notes.db :as db]))

;; NAVIGATION

(re-frame/reg-event-fx
 ::navigate
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [_ [_ handler]]
            {:navigate handler}))

(re-frame/reg-event-fx
 ::set-active-panel
   #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ active-panel]]
   {:db (assoc db :active-panel active-panel)}))

;; PERSON

(re-frame/reg-event-fx
 ::save-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ person]]
            (let [{fname :first-name lname :last-name} person
                  name (lower-case (str fname "-" lname))]
              {:db (assoc-in db [:people (keyword name)] person)})))

(re-frame/reg-event-fx
 ::set-active-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ person-id]]
            (let [person (get-in db [:people (keyword person-id)])]
              {:db (assoc-in db [:active-person] person)})))

(re-frame/reg-event-fx
 ::reset-active-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ person-id]]
            (let [person (get-in db [:person])]
              {:db (assoc-in db [:active-person] person)})))

;; TASK


(re-frame/reg-event-fx
 ::save-task
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ [person task]]]
            (let []
              {:db (assoc-in db [:people (keyword person) :tasks (keyword (:task-id task))] task)})))

(re-frame/reg-event-fx
 ::set-active-task
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ [person task-id]]]
            (let [task (get-in db [:people (keyword person) :tasks (keyword task-id)])]
              {:db (assoc-in db [:active-task] task)})))

(re-frame/reg-event-fx
 ::toggle-task-status
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ [person task-id]]]
            (let [task-complete? (get-in db [:people (keyword person) :tasks (keyword task-id) :completed])]
              {:db (assoc-in db [:people (keyword person) :tasks (keyword task-id) :completed] (not task-complete?))})))

;; DB

(re-frame/reg-event-db
 ::initialize-db
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [_ _]
            db/default-db))
