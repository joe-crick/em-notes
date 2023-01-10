(ns em-notes.events
  (:require
   [re-frame.core :as re-frame]
   [em-notes.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   ))

(re-frame/reg-event-db
 ::initialize-db
   #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [_ _]
   db/default-db))

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
