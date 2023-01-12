(ns em-notes.i18n.tr
  (:require [taoensso.tempura :as tempura :refer [tr]]
           [em-notes.i18n.dev-translations :refer [translations-dict]]))

(defn grab
  [resource & params]
  (let [lang :en]
    (tr {:dict translations-dict} [lang :en] [resource] (vec params))))