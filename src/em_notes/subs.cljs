(ns em-notes.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::active-panel
 (fn [db _]
   (:active-panel db)))

(re-frame/reg-sub
 ::people
 (fn [db _]
   (:people db)))

(re-frame/reg-sub
 ::teams
 (fn [db _]
   (:teams db)))

(re-frame/reg-sub
 ::toasts
 (fn [db _]
   (:toasts db)))

(re-frame/reg-sub
 ::active-team
 (fn [db _]
   (:active-team db)))

(re-frame/reg-sub
 ::active-person
 (fn [db _]
   (:active-person db)))

(re-frame/reg-sub
 ::active-task
 (fn [db _]
   (:active-task db)))

(re-frame/reg-sub
 ::tasks
 (fn [db _]
   (:tasks db)))

(re-frame/reg-sub
 ::active-one-on-one
 (fn [db _]
   (:active-one-on-one db)))

(re-frame/reg-sub
 ::active-growth-metric
 (fn [db _]
   (:active-growth-metric db)))

(re-frame/reg-sub
 ::active-perf
 (fn [db _]
   (:active-perf db)))

(re-frame/reg-sub
 ::active-home-view
 (fn [db _]
   (:active-home-view db)))

(re-frame/reg-sub
 ::active-capacity
 (fn [db _]
   (:active-capacity db)))

(re-frame/reg-sub
 ::modal
 (fn [db _]
   (:modal db)))

(re-frame/reg-sub
 ::confirm
 (fn [db _]
   (:confirm db)))

(re-frame/reg-sub
 ::initialised
 (fn [db _]
   (:initialised db)))