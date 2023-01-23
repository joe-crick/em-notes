(ns em-notes.views.home
  (:require
   [em-notes.views.people.people :refer [people]]
   [em-notes.i18n.tr :refer [grab]]))

(defn home []
  [:section.mt-5
   [:div.container
    [:h1.title
     (grab :home/title)]
    
    [people]]])