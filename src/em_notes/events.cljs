(ns em-notes.events
  (:require
   [re-frame.core :as re-frame]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [em-notes.lib.lower-case :refer [lower-case]]
   [em-notes.i18n.tr :refer [grab]]
   [em-notes.lib.notification-types :refer [notify]]
   [em-notes.db :as db]))

;; NAVIGATION

(re-frame/reg-event-fx
 ::navigate
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [_ [_ handler]]
            {:navigate handler}))

(re-frame/reg-event-db
 ::set-active-panel
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ active-panel]] 
            (assoc db :active-panel active-panel)))

;; TOASTS

;; Toast is an array [Message Type]
(re-frame/reg-event-fx
 ::show-toasts
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ toasts]]
            (let [toasts (conj (get-in db [:toasts]) toasts)] 
              {:db (assoc-in db [:toasts] toasts)
               :dispatch-later [{:ms 3500 :dispatch [::clear-toasts]}]})))

(re-frame/reg-event-db
 ::clear-toasts
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ _]]
            (assoc-in db [:toasts] [])))


;; PERSON

(re-frame/reg-event-fx
 ::save-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ person]]
            (let [{fname :first-name lname :last-name} person
                  name (lower-case (str fname "-" lname))]
              {:db (assoc-in db [:people (keyword name)] person)
               :dispatch [::show-toasts [(grab :form/saved) (:is-success notify)]]})))

(re-frame/reg-event-db
 ::set-active-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ person-id]]
            (let [person (get-in db [:people (keyword person-id)])]
              (assoc-in db [:active-person] person))))

(re-frame/reg-event-db
 ::reset-active-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ _]]
            (let [person (get-in db [:person])]
              (assoc-in db [:active-person] person))))


;; TASK


(re-frame/reg-event-db
 ::save-task
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ [_ [person task]]]]
            (assoc-in db [:people (keyword person) :tasks (keyword (:task-id task))] task)))

(re-frame/reg-event-db
 ::set-active-task
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ [_ [person task-id]]]]
            (let [task (get-in db [:people (keyword person) :tasks (keyword task-id)])]
              (assoc-in db [:active-task] task))))

(re-frame/reg-event-db
 ::toggle-task-status
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ [person task-id]]]
            (let [task-complete? (get-in db [:people (keyword person) :tasks (keyword task-id) :completed])]
              (assoc-in db [:people (keyword person) :tasks (keyword task-id) :completed] (not task-complete?)))))

;; DB

(re-frame/reg-event-db
 ::initialize-db
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [_ _]
            db/default-db))
