(ns em-notes.lib.show-modal
  (:require
   [em-notes.events :as events]
   [re-frame.core :as rf]))

(defn show-modal [title content]
  (rf/dispatch [::events/set-modal {:title title
                                    :content content
                                    :display "is-block"}])
  )