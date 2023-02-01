(ns em-notes.views.home
  (:require [em-notes.components.tabbed-view :refer [tabbed-view]]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.subs :as subs]
            [em-notes.views.people.people :refer [people]]
            [em-notes.views.teams.teams :refer [teams]]
            [re-frame.core :as rf]))

(defn home []
  (let [active-person (rf/subscribe [::subs/active-person])
        title (str (:first-name @active-person) " " (:last-name @active-person))]
    (fn []
      [:div
       [tabbed-view {:tab-navs [[:people (grab :people/title)]
                                [:teams (grab :teams/title)]]
                     :views {:people people
                             :teams teams}
                     :action-buttons []
                     :title title}]])))