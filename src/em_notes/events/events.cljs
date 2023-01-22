(ns em-notes.events.events
  (:require
   [re-frame.core :as re-frame]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [em-notes.lib.lower-case :refer [lower-case]]
   [em-notes.db :as db]))

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

(re-frame/reg-event-fx
 ::save-person
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ person]]
            (let [{fname :first-name lname :last-name} person
                  name (lower-case (str fname "-" lname))]
              {:db (assoc-in db [:people (keyword name)] person)})))

(re-frame/reg-event-db
 ::initialize-db
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [_ _]
            db/default-db))
