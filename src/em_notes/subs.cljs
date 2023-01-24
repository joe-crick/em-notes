(ns em-notes.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::active-panel
 (fn [db _]
   (:active-panel db)))

(re-frame/reg-sub
 ::people
 (fn [db _]
   (:people db)))

(re-frame/reg-sub
 ::toasts
 (fn [db _]
   (:toasts db)))

(re-frame/reg-sub
 ::active-person
 (fn [db _]
   (:active-person db)))

(re-frame/reg-sub
 ::active-task
 (fn [db _]
   (:active-task db)))