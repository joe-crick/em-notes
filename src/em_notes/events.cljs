(ns em-notes.events
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [em-notes.db :as db]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.dissoc-in :refer [dissoc-in]]
            [em-notes.lib.get-unid :refer [get-unid]]
            [em-notes.lib.is-blank :refer [is-blank?]]
            [em-notes.lib.notification-types :refer [notify]]
            [em-notes.lib.person.get-person-id :refer [get-person-id]]
            [em-notes.lib.person.get-sub-person :refer [get-sub-person]]
            [em-notes.lib.person.person-full-name :refer [person-full-name]]
            [em-notes.networking.api :as api :refer [del-person]]
            [em-notes.lib.team.get-team-by-id :refer [get-team-by-id]]
            [em-notes.lib.task.get-task-entity-id :refer [get-task-entity-id]]
            [em-notes.lib.task.get-task-type :refer [get-task-type]]
            [em-notes.lib.filter-map-on-prop :refer [replace-map-by-prop, filter-not-on-prop, update-map-in-vector]]
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
            (let [new-team? (is-blank? :team-id team)
                  team-id (if new-team? (str (random-uuid)) (:team-id team))]
              {:db (assoc-in db [:teams (keyword team-id)] (assoc team :team-id team-id))
               :fx [[:dispatch [::show-toasts [(grab :form/saved) (:is-success notify)]]]
                    [:dispatch [::set-active-team (:team-id team)]]
                    [:dispatch [::commit-db]]
                    ]})))

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

(re-frame/reg-event-fx
 ::get-active-person
 (fn [_ [_ person-id]]
   (api/get-person person-id (fn [person]
                               (re-frame/dispatch [::set-active-person person])))
   {}))


(re-frame/reg-event-db
 ::set-active-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ person]]
            (assoc db :active-person person)))

;; BEGIN person navigation

(re-frame/reg-event-fx
 ::show-person
 (fn [_ [_ person-id]]
   (api/get-person person-id (fn [person]
                               (re-frame/dispatch [::set-returned-person person])))
   {}))

(re-frame/reg-event-fx
 ::set-returned-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ person]]
            {:db  (assoc db :active-person person)
             :fx [[:dispatch [::navigate #js{:name "person"} (str "id=" (:person-id person))]]]}))

;; END person navigation

; Save Person calls Save Team, which updates the database on the server
; This is how the subset of people are saved to the app-dp server-side.
(re-frame/reg-event-fx
 ::save-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ person]]
            (let [person-id (get-unid :person-id person)
                  full-name (person-full-name person)
                  new-person (assoc person :full-name full-name :person-id person-id)
                  team (get-team-by-id (:teams db) (:team person))
                  new-team (assoc team :people (update-map-in-vector (:people team) {:value person-id :label full-name} :value))]
              {:db (assoc-in db [:people (keyword person-id)] (get-sub-person new-person))
               :fx [[:dispatch [::show-toasts [(grab :form/saved) (:is-success notify)]]]
                    [:dispatch [::commit-person new-person]]
                    ;; [:dispatch [::save-team new-team]]
                    [:dispatch [::set-active-person new-person]]
                    ]})))

(re-frame/reg-event-fx
 ::delete-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ person]]
            {:db (dissoc-in db [:people] (keyword (:person-id person)))
             :fx [[:dispatch [::show-toasts [(grab :form/deleted) (:is-success notify)]]]
                  [:dispatch [::reset-active-person]]
                  [:dispatch [::commit-db]]
                  [:dispatch [::commit-person-delete person]]
                  [:dispatch [::navigate "/"]]]}))

(re-frame/reg-event-fx
 ::create-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ _]]
            (let [person (get-in db [:person])]
              {:db  (assoc db :active-person person)
               :fx [[:dispatch [::navigate #js{:name "person"}]]]})))

(re-frame/reg-event-fx
 ::commit-person-delete
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ person]]
            (del-person person)
            {}))


(re-frame/reg-event-fx
 ::reset-active-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ _]]
            (let [person (get-in db [:person])]
              {:db  (assoc db :active-person person)
               :fx [[:dispatch [::navigate "/"]]]})))


(re-frame/reg-event-db
 ::set-person-field
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ val-update]]
            (let [[property value] val-update]
              (assoc-in db (apply conj [:active-person] property) value))))


;; GENERIC

(re-frame/reg-event-fx
 ::save-item
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person item item-set-key item-id-key data-mod] data
                  item-id (get-unid item-id-key item)
                  updated-item (assoc item item-id-key item-id)
                  new-person (assoc-in person [:data item-set-key (keyword item-id)] (if (nil? data-mod) updated-item (data-mod updated-item)))]
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
                    [:dispatch [::cancel-active (keyword (str "active-" item)) ((keyword (str "active-" item)) db)]]
                    [:dispatch [::reset-modal]]
                    [:dispatch [::commit-person new-person]]]})))

(re-frame/reg-event-fx
 ::edit-item
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [{person :person
                   item :item
                   item-form :item-form
                   active-item-key :active-item-key
                   title :title} data
                  person-id (get-person-id person)]
              {:db (assoc db active-item-key item)
               :fx [[:dispatch [::get-active-person person-id]]
                    [:dispatch [::set-modal {:title title
                                             :content item-form
                                             :display "is-block"}]]
                    [:dispatch [::commit-db]]]})))

(re-frame/reg-event-fx
 ::cancel-active
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ active-key default]]
            {:db (assoc db active-key default)
             :fx [[:dispatch [::reset-modal]]]}))


;; TASK

(re-frame/reg-event-fx
 ::get-all-tasks
 (fn [_ _]
   (api/get-tasks (fn [tasks]
                    (re-frame/dispatch [::set-all-tasks tasks])))
   {}))


(re-frame/reg-event-db
 ::set-all-tasks
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ tasks]]
            (assoc db :tasks tasks)))
 
(re-frame/reg-event-fx
 ::edit-task
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[entity-id task task-view] data
                  person? (= "person" (get-task-type task db))
                  get-active (if person? [:dispatch [::get-active-person entity-id]] [:dispatch [::set-active-team entity-id]])]
              {:db (assoc db :active-task task)
               :fx [get-active
                    [:dispatch-later [{:ms 300 :dispatch [::set-modal {:title (grab :task/title)
                                                                       :content task-view
                                                                       :display "is-block"}]}]]]})))

(re-frame/reg-event-db
 ::prep-new-task
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ entity]]
            (let [default-task (:default-task db)
                  blank-task (assoc default-task :owner-id (get-task-entity-id entity))]
              (assoc db :active-task blank-task))))

(re-frame/reg-event-fx
 ::cancel-task
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ _]]
            {:db (assoc db :active-task (:default-task db))
             :fx [[:dispatch [::reset-modal]]]}))

(re-frame/reg-event-fx
 ::save-task
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[entity task task-type] data
                  task-id (get-unid :task-id task)
                  updated-task (assoc task :task-id task-id :completed (boolean (:completed task)) :owner-id (get-task-entity-id entity))
                  new-entity (assoc-in entity [:data :tasks (keyword task-id)] updated-task)
                  update (if (= task-type "person")
                           [:dispatch [::commit-person new-entity]]
                           [:dispatch [::save-team new-entity]])]
              {:db (assoc db (keyword (str "active-" task-type)) new-entity)
               :fx [[:dispatch [::show-toasts [(grab :form/saved) (:is-success notify)]]]
                    [:dispatch [::reset-modal]]
                    ;; Not a perfect solution, but... :)
                    [:dispatch-later [{:ms 500 :dispatch [::get-all-tasks]}]]
                    update]})))

(re-frame/reg-event-fx
 ::toggle-task-status
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[entity task task-type] data
                  task-id (get-unid :task-id task)
                  updated-task (assoc task :task-id task-id :completed (not (boolean (:completed task))))
                  new-entity (assoc-in entity [:data :tasks (keyword task-id)] updated-task)
                  update (if (= task-type "person")
                           [:dispatch [::commit-person new-entity]]
                           [:dispatch [::save-team new-entity]])]
              {:db (assoc db (keyword (str "active-" task-type)) new-entity)
               :fx [update]})))

(re-frame/reg-event-fx
 ::toggle-task-all-status
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[entity task] data
                  task-type (get-task-type task db)
                  task-id (get-unid :task-id task)
                  updated-task (assoc task :task-id task-id :completed (not (boolean (:completed task))))
                  new-entity (assoc-in entity [:data :tasks (keyword task-id)] updated-task)
                  tasks (:tasks db)
                  update (if (= task-type "person")
                           [:dispatch [::commit-person new-entity]]
                           [:dispatch [::save-team new-entity]])]
              {:db (assoc db :tasks (replace-map-by-prop tasks :task-id updated-task))
               :fx [update]})))

(re-frame/reg-event-fx
 ::delete-task-all
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person task] data
                  task-id (:task-id task) 
                  tasks (filter-not-on-prop (:tasks db) [:task-id] task-id)]
              (api/get-person (:person-id person) (fn [p]
                                                    (let [new-person (dissoc-in p [:data :tasks] (keyword task-id))]
                                                      (re-frame/dispatch [::commit-person new-person]))))
              {:db (assoc db :tasks tasks)
               :fx []})))


;; GROWTH METRIC

(re-frame/reg-event-fx
 ::edit-metric
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person metric metric-view] data]
              {:db db
               :fx [[:dispatch [::edit-item {:person person
                                             :item metric
                                             :item-form metric-view
                                             :active-item-key :active-growth-metric
                                             :title (grab :growth-metric/title)}]]]})))

(re-frame/reg-event-fx
 ::cancel-metric
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ _]]
            {:db (assoc db :active-growth-metric (:default-metric db))
             :fx [[:dispatch [::reset-modal]]]}))


;; PERFORMANCE

(re-frame/reg-event-fx
 ::edit-perf
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person perf perf-view] data]
              {:db db
               :fx [[:dispatch [::edit-item {:person person
                                             :item perf
                                             :item-form perf-view
                                             :active-item-key :active-perf
                                             :title (grab :perf/title)}]]]})))

(re-frame/reg-event-fx
 ::cancel-perf
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ _]]
            {:db (assoc db :active-perf (:default-perf db))
             :fx [[:dispatch [::reset-modal]]]}))


;; ONE ON ONE

(re-frame/reg-event-fx
 ::edit-one-on-one
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person one-on-one one-on-one-view] data]
              {:db db
               :fx [[:dispatch [::edit-item {:person person
                                             :item one-on-one
                                             :item-form one-on-one-view
                                             :active-item-key :active-one-on-one
                                             :title (grab :one-on-one/title)}]]]})))

(re-frame/reg-event-fx
 ::cancel-one-on-one
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ _]]
            {:db (assoc db :active-one-on-one (:default-one-on-one db))
             :fx [[:dispatch [::reset-modal]]]}))


;; CAPACITY

(re-frame/reg-event-fx
 ::save-capacity
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[team capacity] data
                  capacity-id (get-unid :capacity-id capacity)
                  updated-capacity (assoc capacity :capacity-id capacity-id :completed (boolean (:completed capacity)))
                  new-team (assoc-in team [:data :capacities (keyword capacity-id)] updated-capacity)]
              {:db (assoc-in db [:teams (keyword (:team-id new-team))] new-team)
               :fx [[:dispatch [::show-toasts [(grab :form/saved) (:is-success notify)]]]
                    [:dispatch [::reset-modal]]
                    [:dispatch [::set-active-team (:team-id new-team)]]
                    [:dispatch [::commit-db]]]})))

(re-frame/reg-event-fx
 ::delete-capacity
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[team capacity] data
                  item-id (:capacity-id capacity)
                  new-team (dissoc-in team [:data :capacities] (keyword item-id))]
              {:db (assoc-in db [:teams (keyword (:team-id new-team))] new-team)
               :fx [[:dispatch [::show-toasts [(grab :form/deleted) (:is-success notify)]]]
                    [:dispatch [::cancel-active :active-capacity (:default-capacity db)]]
                    [:dispatch [::reset-modal]]
                    [:dispatch [::set-active-team (:team-id new-team)]]
                    [:dispatch [::commit-db]]]})))

(re-frame/reg-event-fx
 ::edit-capacity
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ data]]
            (let [[person capacity capacity-view] data]
              {:db db
               :fx [[:dispatch [::edit-item {:person person
                                             :item capacity
                                             :item-form capacity-view
                                             :active-item-key :active-capacity
                                             :title (grab :capacity/title)}]]]})))

(re-frame/reg-event-fx
 ::cancel-capacity
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ _]]
            {:db (assoc db :active-capacity (:default-capacity db))
             :fx [[:dispatch [::reset-modal]]]}))


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

;; ACTIVE CONTEXT

(re-frame/reg-event-db
 ::set-active-context
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ active-view]]
            (assoc db :active-context active-view)))

(re-frame/reg-event-fx
 ::reset-active-context
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db [_ _]]
            (assoc db :active-context "")))

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
