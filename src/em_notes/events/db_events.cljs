(ns em-notes.events.db-events
  (:require
   [re-frame.core :as re-frame]
   [em-notes.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]))

(re-frame/reg-event-db
 ::initialize-db
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [_ _]
            db/default-db))

(re-frame/reg-event-db
 ::create-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [db {:keys [first-name last-name team]}]
            db/default-db))