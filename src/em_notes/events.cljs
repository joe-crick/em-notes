(ns em-notes.events
  (:require
   [re-frame.core :as re-frame]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [em-notes.lib.lower-case :refer [lower-case]]
   [em-notes.i18n.tr :refer [grab]]
   [em-notes.lib.notification-types :refer [notify]]
   [em-notes.lib.dissoc-in :refer [dissoc-in]]
   [em-notes.db :as db]))

;; NAVIGATION

(re-frame/reg-event-fx
 ::navigate
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [_ [_ handler params]] 
            ;; Calls a registered effect (see routing.cljs)
            {:navigate [handler params]}))

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
              {:db (assoc db :toasts toasts)
               :fx [[:dispatch-later [{:ms 3500 :dispatch [::clear-toasts]}]]]
               })))

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
                  person-id (lower-case (str fname "-" lname))]
              {:db (assoc-in db [:people (keyword person-id)] person)
               :fx [[:dispatch [::show-toasts [(grab :form/saved) (:is-success notify)]]]
                    [:dispatch [::set-active-person person-id]]]})))

(re-frame/reg-event-fx
 ::delete-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ person]]
            (let [{fname :first-name lname :last-name} person
                  name (lower-case (str fname "-" lname))]
              {:db (dissoc-in db [:people] (keyword name))
               :fx [[:dispatch [::show-toasts [(grab :form/deleted) (:is-success notify)]]]
                    [:dispatch [::reset-active-person]]
                    [:dispatch [::navigate "/"]]]})))

(re-frame/reg-event-db
 ::set-active-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ person-id]]
            (let [person (get-in db [:people (keyword person-id)])]
              (assoc db :active-person person))))

(re-frame/reg-event-db
 ::reset-active-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ _]]
            (let [person (get-in db [:person])]
              (assoc db :active-person person))))


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
              (assoc db :active-task task))))

(re-frame/reg-event-db
 ::toggle-task-status
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ [person task-id]]]
            (let [task-complete? (get-in db [:people (keyword person) :tasks (keyword task-id) :completed])]
              (assoc-in db [:people (keyword person) :tasks (keyword task-id) :completed] (not task-complete?)))))


;; MODAL

(re-frame/reg-event-db
 ::set-modal
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ modal-config]]
            (assoc db :modal modal-config)))

;; DB

(re-frame/reg-event-db
 ::initialize-db
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [_ _]
            db/default-db))
