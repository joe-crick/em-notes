(ns em-notes.lib.show-confirm
  (:require
   [re-frame.core :as rf]
   [em-notes.events :as events]))

(defn show-confirm [msg on-confirm]
  (prn "on-confirm: " on-confirm)
  (rf/dispatch [::events/show-confirm {:msg msg
                                       :on-confirm on-confirm
                                       :display "is-block"}]))