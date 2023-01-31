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
   [em-notes.lib.get-person-id :refer [get-person-id]]))

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
                  new-task? (= (:task-id task) "")
                  task-id (if new-task? (str (random-uuid)) (:task-id task))
                  updated-task (assoc task :task-id task-id :completed (boolean (:completed task)))] 
              {:db (assoc-in db [:people (keyword person-id) :tasks (keyword task-id)] updated-task)
               :fx [[:dispatch [::show-toasts [(grab :form/saved) (:is-success notify)]]]
                    [:dispatch [::set-active-person person-id]]
                    [:dispatch [::set-modal (:default-modal db)]]
                    [:dispatch [::save-db]]]})))

(re-frame/reg-event-fx
 ::edit-task
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person task task-view] data
                  person-id (get-person-id person)]
              {:db (assoc db :active-task task)
               :fx [[:dispatch [::set-active-person person-id]]
                    [:dispatch [::set-modal {:title (grab :task/title)
                                             :content task-view
                                             :display "is-block"}]]
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
                    [:dispatch [::set-active-person person-id]]
                    [:dispatch [::save-db]]]})))

(re-frame/reg-event-fx
 ::cancel-task
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ _]]
            {:db (assoc db :active-task (:default-task db))
             :fx [[:dispatch [::set-modal (:default-modal db)]]]}))

(re-frame/reg-event-db
 ::set-active-task
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ [_ [person task-id]]]]
            (let [task (get-in db [:people (keyword person) :tasks (keyword task-id)])]
              (assoc db :active-task task))))

(re-frame/reg-event-fx
 ::toggle-task-status
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person task] data
                  person-id (get-person-id person)
                  task-complete? (:completed task)
                  task-id (:task-id task)]
              {:db (assoc-in db [:people (keyword person-id) :tasks (keyword task-id) :completed] (not task-complete?))
               :fx [[:dispatch [::set-active-person person-id]]
                    [:dispatch [::save-db]]]})))


;; GROWTH METRIC

(re-frame/reg-event-fx
 ::save-metric
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person metric] data
                  person-id (get-person-id person)
                  new-metric? (nil? (:metric-id metric))
                  metric-id (if new-metric? (str (random-uuid)) (:metric-id metric))
                  updated-metric (assoc metric :metric-id metric-id)]
              {:db (assoc-in db [:people (keyword person-id) :growth-metrics (keyword metric-id)] updated-metric)
               :fx [[:dispatch [::show-toasts [(grab :form/saved) (:is-success notify)]]]
                    [:dispatch [::set-active-person person-id]]
                    [:dispatch [::set-modal (:default-modal db)]]
                    [:dispatch [::save-db]]]})))

;; TODO - Fix this :)

(re-frame/reg-event-fx
 ::edit-metric
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person metric metric-view] data
                  person-id (get-person-id person)]
              {:db (assoc db :active-metric metric)
               :fx [[:dispatch [::set-active-person person-id]]
                    [:dispatch [::set-modal {:title (grab :metric/title)
                                             :content metric-view
                                             :display "is-block"}]]
                    [:dispatch [::save-db]]]})))

(re-frame/reg-event-fx
 ::delete-metric
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person metric] data
                  person-id (get-person-id person)
                  metric-id (:metric-id metric)]
              {:db (dissoc-in db [:people (keyword person-id) :growth-metrics] (keyword metric-id))
               :fx [[:dispatch [::show-toasts [(grab :form/deleted) (:is-success notify)]]]
                    [:dispatch [::cancel-metric]]
                    [:dispatch [::set-modal (:default-modal db)]]
                    [:dispatch [::set-active-person person-id]]
                    [:dispatch [::save-db]]]})))

(re-frame/reg-event-fx
 ::cancel-metric
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ _]]
            {:db (assoc db :active-metric (:default-metric db))
             :fx [[:dispatch [::set-modal (:default-modal db)]]]}))

(re-frame/reg-event-db
 ::set-active-metric
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ [_ [person metric-id]]]]
            (let [metric (get-in db [:people (keyword person) :growth-metrics (keyword metric-id)])]
              (assoc db :active-metric metric))))

(re-frame/reg-event-fx
 ::toggle-metric-status
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person metric] data
                  person-id (get-person-id person)
                  metric-complete? (:completed metric)
                  metric-id (:metric-id metric)]
              {:db (assoc-in db [:people (keyword person-id) :growth-metrics (keyword metric-id) :completed] (not metric-complete?))
               :fx [[:dispatch [::set-active-person person-id]]
                    [:dispatch [::save-db]]]})))


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
