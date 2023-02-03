(ns em-notes.views.home
  (:require [em-notes.components.tabbed-view :refer [tabbed-view]]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.views.people.people :refer [people]]
            [em-notes.views.teams.teams :refer [teams]]))

(defn home []
  (fn []
    [:div
     [tabbed-view {:tab-navs [[:people (grab :people/title)]
                              [:teams (grab :teams/title)]]
                   :views {:people people
                           :teams teams}
                   :action-buttons []
                   :title (grab :home/home)}]]))