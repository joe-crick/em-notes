(ns em-notes.events
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [em-notes.db :as db]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.dissoc-in :refer [dissoc-in]]
            [em-notes.lib.get-person-id :refer [get-person-id]]
            [em-notes.lib.get-sub-person :refer [get-sub-person]]
            [em-notes.lib.notification-types :refer [notify]]
            [em-notes.lib.is-blank-id :refer [is-blank-id]]
            [em-notes.lib.person-full-name :refer [person-full-name]]
            [em-notes.lib.get-unid :refer [get-unid]]
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
            (let [new-team? (is-blank-id :team-id team)
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
            (assoc db :active-person person)))

(re-frame/reg-event-fx
 ::save-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ person]]
            (let [person-id (get-unid :person-id person)
                  full-name (person-full-name person)
                  new? (is-blank-id :person-id person)
                  new-person (if new? (assoc person :full-name full-name :person-id person-id) person)
                  new-db (assoc-in db [:people (keyword person-id)] (get-sub-person new-person))]
              {:db (assoc-in db [:people (keyword person-id)] new-db)
               :fx [[:dispatch [::show-toasts [(grab :form/saved) (:is-success notify)]]]
                    [:dispatch [::commit-db]]
                    [:dispatch [::commit-person new-person]]
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
                  task-id (get-unid :task-id task)
                  updated-task (assoc task :task-id task-id :completed (boolean (:completed task)))
                  new-person (assoc-in person [:data :tasks (keyword task-id)] updated-task)] 
              {:db (assoc db :active-person new-person)
               :fx [[:dispatch [::show-toasts [(grab :form/saved) (:is-success notify)]]]
                    [:dispatch [::reset-modal]]
                    [:dispatch [::commit-person new-person]]]})))

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
 ::cancel-task
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ _]]
            {:db (assoc db :active-task (:default-task db))
             :fx [[:dispatch [::reset-modal]]]}))

(re-frame/reg-event-db
 ::set-active-task
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ [_ [person task-id]]]]
            (let [task (get-in db [:people (keyword person) :data :tasks (keyword task-id)])]
              (assoc db :active-task task))))

(re-frame/reg-event-fx
 ::toggle-task-status
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person task] data
                  person-id (get-person-id person)
                  task-complete? (:completed task)
                  task-id (:task-id task)]
              {:db (assoc-in db [:people (keyword person-id) :data :tasks (keyword task-id) :completed] (not task-complete?))
               :fx [[:dispatch [::get-active-person person-id]]
                    [:dispatch [::commit-db]]]})))



;; GENERIC

(re-frame/reg-event-fx
 ::save-item
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person item item-set-key item-id-key data-mod] data
                  new? (is-blank-id item)
                  item-id (get-unid item-id-key item)
                  updated-item (if new? (assoc item item-id-key item-id) item) 
                  new-person (assoc-in person [:data item-set-key item-id] (if (nil? data-mod) updated-item (data-mod updated-item)))]
              {:db (assoc db :active-person new-person)
               :fx [[:dispatch [::show-toasts [(grab :form/saved) (:is-success notify)]]]
                    [:dispatch [::reset-modal]]
                    [:dispatch [::commit-person new-person]]]})))

(re-frame/reg-event-fx
 ::delete-item
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person item item-set-key item-id-key] data
                  item-id (item-id-key item)
                  new-person (dissoc-in person [:data item-set-key] (keyword item-id))]
              {:db (assoc db :active-person new-person)
               :fx [[:dispatch [::show-toasts [(grab :form/deleted) (:is-success notify)]]]
                    [:dispatch [::cancel-active :active-growth-metric (:default-metric db)]]
                    [:dispatch [::reset-modal]]
                    [:dispatch [::commit-person new-person]]]})))

(re-frame/reg-event-fx
 ::cancel-active
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ active-key default]]
            {:db (assoc db active-key default)
             :fx [[:dispatch [::reset-modal]]]}))

;; GROWTH METRIC

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
 ::cancel-metric
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ _]]
            {:db (assoc db :active-growth-metric (:default-metric db))
             :fx [[:dispatch [::reset-modal]]]}))

(re-frame/reg-event-db
 ::set-active-growth-metric
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ [_ [person metric-id]]]]
            (let [metric (get-in db [:people (keyword person) :data :growth-metrics (keyword metric-id)])]
              (assoc db :active-growth-metric metric))))

;; PERFORMANCE

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
 ::cancel-perf
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ _]]
            {:db (assoc db :active-perf (:default-perf db))
             :fx [[:dispatch [::reset-modal]]]}))

(re-frame/reg-event-db
 ::set-active-perf
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ [_ [person perf-id]]]]
            (let [perf (get-in db [:people (keyword person) :data :perfs (keyword perf-id)])]
              (assoc db :active-perf perf))))

;; ONE ON ONE

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
 ::cancel-one-on-one
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ _]]
            {:db (assoc db :active-one-on-one (:default-one-on-one db))
             :fx [[:dispatch [::reset-modal]]]}))

(re-frame/reg-event-db
 ::set-active-one-on-one
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ [_ [person one-on-one-id]]]]
            (let [one-on-one (get-in db [:people (keyword person) :data :one-on-ones (keyword one-on-one-id)])]
              (assoc db :active-one-on-one one-on-one))))


;; MODAL

(re-frame/reg-event-db
 ::set-modal
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ modal-config]]
            (assoc db :modal modal-config)))

(re-frame/reg-event-db
 ::reset-modal
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ _]]
            (assoc db :modal (:default-modal db))))

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