(ns em-notes.events
  (:require
   [re-frame.core :as re-frame]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [em-notes.lib.lower-case :refer [lower-case]]
   [em-notes.i18n.tr :refer [grab]]
   [em-notes.lib.notification-types :refer [notify]]
   [em-notes.lib.dissoc-in :refer [dissoc-in]]
   [em-notes.networking.api :refer [get-app-db, save-app-db]]
   [em-notes.db :as db]
   [em-notes.lib.nab :refer [nab]]
   [em-notes.lib.get-person-id :refer [get-person-id]]
   [re-frame.events :as events]))

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
               :fx [[:dispatch-later [{:ms 3500 :dispatch [::clear-toasts]}]]]})))

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
                    [:dispatch [::set-active-person person-id]]
                    [:dispatch [::save-db]]]})))

(re-frame/reg-event-fx
 ::delete-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ person]]
            (let [{fname :first-name lname :last-name} person
                  name (lower-case (str fname "-" lname))]
              {:db (dissoc-in db [:people] (keyword name))
               :fx [[:dispatch [::show-toasts [(grab :form/deleted) (:is-success notify)]]]
                    [:dispatch [::reset-active-person]]
                    [:dispatch [::save-db]]
                    [:dispatch [::navigate "/"]]]})))

(re-frame/reg-event-db
 ::set-active-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ person-id]]
            (prn "person-id: " person-id)
            (let [person (get-in db [:people (keyword person-id)])]
              (assoc db :active-person person))))

(re-frame/reg-event-fx
 ::reset-active-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ _]]
            (let [person (get-in db [:person])]
              {:db  (assoc db :active-person person)
               :fx [[:dispatch [::navigate "/"]]]})))


;; TASK

(re-frame/reg-event-fx
 ::save-task
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person task] data
                  person-id (get-person-id person)
                  new-task? (nil? (nab :task-id task))
                  task-id (if new-task? (str (random-uuid)) (:task-id task))]
              {:db (assoc-in db [:people (keyword person-id) :tasks (keyword task-id)] (conj (:tasks db) task))
               :fx [[:dispatch [::show-toasts [(grab :form/saved) (:is-success notify)]]]
                    [:dispatch [::set-active-task person-id task-id]]
                    [:dispatch [::set-modal (:default-modal db)]]
                    [:dispatch [::save-db]]]})))

(re-frame/reg-event-fx
 ::delete-task
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person task] data
                  person-id (get-person-id person)
                  task-id (:task-id task)]
              {:db (dissoc-in db [:people (keyword person-id) :tasks] (keyword task-id))
               :fx [[:dispatch [::show-toasts [(grab :form/deleted) (:is-success notify)]]]
                    [:dispatch [::cancel-task]]
                    [:dispatch [::set-modal (:default-modal db)]]
                    [:dispatch [::save-db]]]})))

(re-frame/reg-event-fx
 ::cancel-task
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ _]]
            {:db (dissoc db :active-task)
             :fx [[:dispatch [::set-modal (:default-modal db)]]]}))

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

;; CONFIRM

(re-frame/reg-event-fx
 ::run-confirm
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ event]]
            {:db (assoc db :confirm (:default-confirm db))
             :fx [[:dispatch event]]}))

(re-frame/reg-event-db
 ::show-confirm
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ confirm-config]]
            (assoc db :confirm confirm-config)))

(re-frame/reg-event-db
 ::close-confirm
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ _]]
            (assoc db :confirm (:default-confirm db))))


;; ROUTE QUEUE

(re-frame/reg-event-fx
 ::add-to-route-queue
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ event]]
            (let [is-init? (nil? (:initialised db))
                  update (if is-init? (assoc db :init-queue (conj (:init-queue db) event)) db)
                  events (if is-init? [] event)]
              {:db update
               :fx [events]})))

;; DB

(re-frame/reg-event-db
 ::initialize-db
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [_ _]
            (get-app-db (fn [db]
                          (re-frame/dispatch [::set-init-db db])))
            db/default-db))

(re-frame/reg-event-fx
 ::set-init-db
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ api-db]]
            (let [events (:init-queue db)]
              {:db (assoc db :people api-db :initialised true)
               :fx events})))

(re-frame/reg-event-db
 ::save-db
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ _]]
            (save-app-db (:people db))
            db))
