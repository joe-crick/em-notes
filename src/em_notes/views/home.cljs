(ns em-notes.views.home
  (:require
   [em-notes.views.person :refer [people]]
   [em-notes.i18n.tr :refer [grab]]))

(defn home []
  [:section.mt-5
   [:div.container
    [:h1.title
     (grab :home/title)]
    
    [:div.container
     
     [:ul
      [:li [:a "People"]]
      [:li [:a "Team"]]]]

    [people]]])