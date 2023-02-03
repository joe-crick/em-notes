(ns em-notes.views.home
  (:require [em-notes.components.tabbed-view :refer [tabbed-view]]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.views.people.people :refer [people]]
            [em-notes.subs :as subs]
            [re-frame.core :as re-frame]
            [em-notes.views.teams.teams :refer [teams]]))

(defn home []
  (let [active-view (re-frame/subscribe [::subs/active-home-view])
        view (if (nil? @active-view) :people @active-view)]
    (fn []
      [:div
       [tabbed-view {:tab-navs [[:people (grab :people/title)]
                                [:teams (grab :teams/title)]]
                     :views {:people people
                             :teams teams}
                     :action-buttons []
                     :title (grab :home/home)
                     :active-view view}]])))