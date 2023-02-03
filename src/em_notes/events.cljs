(ns em-notes.events
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [em-notes.db :as db]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.dissoc-in :refer [dissoc-in]]
            [em-notes.lib.get-person-id :refer [get-person-id]]
            [em-notes.lib.notification-types :refer [notify]]
            [em-notes.networking.api :as api]
            [re-frame.core :as re-frame]))

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


;; TEAM

(re-frame/reg-event-fx
 ::save-team
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ team]]
            (let [new-team? (nil? (:team-id team))
                  team-id (if new-team? (str (random-uuid)) (:team-id team))]
              {:db (assoc-in db [:teams (keyword team-id)] (assoc team :team-id team-id))
               :fx [[:dispatch [::show-toasts [(grab :form/saved) (:is-success notify)]]]
                    [:dispatch [::set-active-team (:team-id team)]]
                    [:dispatch [::commit-db]]]})))

(re-frame/reg-event-fx
 ::delete-team
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ team]]
            {:db (dissoc-in db [:teams] (keyword (:team-id team)))
             :fx [[:dispatch [::show-toasts [(grab :form/deleted) (:is-success notify)]]]
                  [:dispatch [::reset-active-team]]
                  [:dispatch [::commit-db]]
                  [:dispatch [::navigate "/"]]]}))

(re-frame/reg-event-db
 ::set-active-team
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ team-id]]
            (let [team (get-in db [:teams (keyword team-id)])]
              (assoc db :active-team team))))

(re-frame/reg-event-fx
 ::reset-active-team
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ _]]
            (let [team (get-in db [:team])]
              {:db  (assoc db :active-team team)
               :fx [[:dispatch [::navigate "/"]]]})))


;; PERSON

(re-frame/reg-event-db
 ::get-active-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ person-id]]
            (api/get-person person-id (fn [person]
                                        (re-frame/dispatch [::set-active-person person])))
            db))

(re-frame/reg-event-db
 ::set-active-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ person]]
            (prn "returned person" person)
            (assoc db :active-person person)))

(re-frame/reg-event-fx
 ::save-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ person]]
            (let [new-person? (nil? (:person-id person))
                  person-id (if new-person? (str (random-uuid)) (:person-id person))
                  {fname :first-name lname :last-name} person
                  full-name (str fname " " lname)
                  ]
              {:db (assoc-in db [:people (keyword person-id)] (assoc person :full-name full-name :person-id person-id))
               :fx [[:dispatch [::show-toasts [(grab :form/saved) (:is-success notify)]]]
                    [:dispatch-sync [::commit-db]]
                    [:dispatch-sync [::commit-person person]]
                    [:dispatch [::get-active-person person-id]]]})))

(re-frame/reg-event-fx
 ::delete-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ person]]
            {:db (dissoc-in db [:people] (keyword (:person-id person)))
             :fx [[:dispatch [::show-toasts [(grab :form/deleted) (:is-success notify)]]]
                  [:dispatch [::reset-active-person]]
                  [:dispatch [::commit-db]]
                  [:dispatch [::navigate "/"]]]}))

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
                    [:dispatch [::get-active-person person-id]]
                    [:dispatch [::set-modal (:default-modal db)]]
                    [:dispatch [::commit-db]]]})))

(re-frame/reg-event-fx
 ::edit-task
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person task task-view] data
                  person-id (get-person-id person)]
              {:db (assoc db :active-task task)
               :fx [[:dispatch [::get-active-person person-id]]
                    [:dispatch [::set-modal {:title (grab :task/title)
                                             :content task-view
                                             :display "is-block"}]]
                    [:dispatch [::commit-db]]]})))

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
                    [:dispatch [::get-active-person person-id]]
                    [:dispatch [::commit-db]]]})))

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
               :fx [[:dispatch [::get-active-person person-id]]
                    [:dispatch [::commit-db]]]})))


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
                    [:dispatch [::get-active-person person-id]]
                    [:dispatch [::set-modal (:default-modal db)]]
                    [:dispatch [::commit-db]]]})))

(re-frame/reg-event-fx
 ::edit-metric
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person metric metric-view] data
                  person-id (get-person-id person)]
              {:db (assoc db :active-growth-metric metric)
               :fx [[:dispatch [::get-active-person person-id]]
                    [:dispatch [::set-modal {:title (grab :growth-metric/title)
                                             :content metric-view
                                             :display "is-block"}]]
                    [:dispatch [::commit-db]]]})))

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
                    [:dispatch [::get-active-person person-id]]
                    [:dispatch [::commit-db]]]})))

(re-frame/reg-event-fx
 ::cancel-metric
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ _]]
            {:db (assoc db :active-growth-metric (:default-metric db))
             :fx [[:dispatch [::set-modal (:default-modal db)]]]}))

(re-frame/reg-event-db
 ::set-active-growth-metric
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ [_ [person metric-id]]]]
            (let [metric (get-in db [:people (keyword person) :growth-metrics (keyword metric-id)])]
              (assoc db :active-growth-metric metric))))

;; PERFORMANCE

(re-frame/reg-event-fx
 ::save-perf
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person perf] data
                  person-id (get-person-id person)
                  new-perf? (nil? (:perf-id perf))
                  perf-id (if new-perf? (str (random-uuid)) (:perf-id perf))
                  updated-perf (assoc perf :perf-id perf-id)]
              {:db (assoc-in db [:people (keyword person-id) :perfs (keyword perf-id)] updated-perf)
               :fx [[:dispatch [::show-toasts [(grab :form/saved) (:is-success notify)]]]
                    [:dispatch [::get-active-person person-id]]
                    [:dispatch [::set-modal (:default-modal db)]]
                    [:dispatch [::commit-db]]]})))

(re-frame/reg-event-fx
 ::edit-perf
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person perf perf-view] data
                  person-id (get-person-id person)]
              {:db (assoc db :active-perf perf)
               :fx [[:dispatch [::get-active-person person-id]]
                    [:dispatch [::set-modal {:title (grab :perf/title)
                                             :content perf-view
                                             :display "is-block"}]]
                    [:dispatch [::commit-db]]]})))

(re-frame/reg-event-fx
 ::delete-perf
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person perf] data
                  person-id (get-person-id person)
                  perf-id (:perf-id perf)]
              {:db (dissoc-in db [:people (keyword person-id) :perfs] (keyword perf-id))
               :fx [[:dispatch [::show-toasts [(grab :form/deleted) (:is-success notify)]]]
                    [:dispatch [::cancel-perf]]
                    [:dispatch [::set-modal (:default-modal db)]]
                    [:dispatch [::get-active-person person-id]]
                    [:dispatch [::commit-db]]]})))

(re-frame/reg-event-fx
 ::cancel-perf
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ _]]
            {:db (assoc db :active-perf (:default-perf db))
             :fx [[:dispatch [::set-modal (:default-modal db)]]]}))

(re-frame/reg-event-db
 ::set-active-perf
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ [_ [person perf-id]]]]
            (let [perf (get-in db [:people (keyword person) :perfs (keyword perf-id)])]
              (assoc db :active-perf perf))))

;; ONE ON ONE

(re-frame/reg-event-fx
 ::save-one-on-one
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person one-on-one] data
                  person-id (get-person-id person)
                  new-one-on-one? (= (:one-on-one-id one-on-one) "")
                  one-on-one-id (if new-one-on-one? (str (random-uuid)) (:one-on-one-id one-on-one))
                  updated-one-on-one (assoc one-on-one :one-on-one-id one-on-one-id)] 
              {:db (assoc-in db [:people (keyword person-id) :one-on-ones (keyword one-on-one-id)] updated-one-on-one)
               :fx [[:dispatch [::show-toasts [(grab :form/saved) (:is-success notify)]]]
                    [:dispatch [::get-active-person person-id]]
                    [:dispatch [::set-modal (:default-modal db)]]
                    [:dispatch [::commit-db]]]})))

(re-frame/reg-event-fx
 ::edit-one-on-one
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person one-on-one one-on-one-view] data
                  person-id (get-person-id person)]
              {:db (assoc db :active-one-on-one one-on-one)
               :fx [[:dispatch [::get-active-person person-id]]
                    [:dispatch [::set-modal {:title (grab :one-on-one/title)
                                             :content one-on-one-view
                                             :display "is-block"}]]
                    [:dispatch [::commit-db]]]})))

(re-frame/reg-event-fx
 ::delete-one-on-one
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person one-on-one] data
                  person-id (get-person-id person)
                  one-on-one-id (:one-on-one-id one-on-one)]
              {:db (dissoc-in db [:people (keyword person-id) :one-on-ones] (keyword one-on-one-id))
               :fx [[:dispatch [::show-toasts [(grab :form/deleted) (:is-success notify)]]]
                    [:dispatch [::cancel-one-on-one]]
                    [:dispatch [::set-modal (:default-modal db)]]
                    [:dispatch [::get-active-person person-id]]
                    [:dispatch [::commit-db]]]})))

(re-frame/reg-event-fx
 ::cancel-one-on-one
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ _]]
            {:db (assoc db :active-one-on-one (:default-one-on-one db))
             :fx [[:dispatch [::set-modal (:default-modal db)]]]}))

(re-frame/reg-event-db
 ::set-active-one-on-one
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ [_ [person one-on-one-id]]]]
            (let [one-on-one (get-in db [:people (keyword person) :one-on-ones (keyword one-on-one-id)])]
              (assoc db :active-one-on-one one-on-one))))


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

;; ACTIVE HOME VIEW

(re-frame/reg-event-db
 ::set-active-home-view
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ active-view]]
            (assoc db :active-home-view active-view)))

(re-frame/reg-event-fx
 ::reset-active-home-view
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ _]]
            (assoc db :active-home-view "")))

;; DB

(re-frame/reg-event-db
 ::initialize-db
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [_ _]
            (api/get-app-db (fn [db]
                          (re-frame/dispatch [::set-init-db db])))
            db/default-db))

(re-frame/reg-event-fx
 ::set-init-db
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ api-db]]
            (let [events (:init-queue db)]
              {:db (assoc db :people (:people api-db) :teams (:teams api-db) :initialised true)
               :fx events})))

(re-frame/reg-event-db
 ::commit-db
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ _]]
            (api/save-app-db {:people (:people db) :teams (:teams db)})
            db))

(re-frame/reg-event-db
 ::commit-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ person]]
            (api/save-person person)
            db))