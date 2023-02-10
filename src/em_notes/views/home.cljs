(ns em-notes.views.home
  (:require [em-notes.components.tabbed-view :refer [tabbed-view]]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.subs :as subs]
            [em-notes.views.people.people :refer [people]]
            [em-notes.views.all-tasks :refer [tasks]]
            [em-notes.views.teams.teams :refer [teams]]
            [re-frame.core :as re-frame]))

(defn home []
  (let [active-view (re-frame/subscribe [::subs/active-home-view])
        view (if (nil? @active-view) :people @active-view)]
    (fn []
      [:div
       [tabbed-view {:tab-navs [[:people (grab :people/title)]
                                [:teams (grab :teams/title)]
                                [:tasks (grab :tasks/title)]]
                     :views {:people people
                             :teams teams
                             :tasks tasks}
                     :action-buttons []
                     :title (grab :home/home)
                     :active-view view} true]])))