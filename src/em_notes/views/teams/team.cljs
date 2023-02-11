(ns em-notes.views.teams.team
  (:require [em-notes.components.tabbed-view :refer [tabbed-view]]
            [em-notes.events :as events]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.css-cls :refer [css-cls]]
            [em-notes.lib.show-confirm :refer [show-confirm]]
            [em-notes.routing.nav :as nav]
            [em-notes.subs :as subs]
            [em-notes.views.performance.perfs :refer [perfs]]
            [em-notes.views.tasks.tasks :refer [tasks]]
            [em-notes.views.teams.capacity.capacities :refer [capacities]]
            [em-notes.views.teams.profile.team-profile :refer [team-profile]]
            [re-frame.core :as rf]))


(defn team []
  (let [active-team (rf/subscribe [::subs/active-team])]
    (fn []
      [:section {:style {:margin-top "-40px"}}
       [:div {:class (css-cls :container)}
        [:button {:class (css-cls :button :is-ghost :mt-5) :on-click #(nav/go :home)} (str "< " (grab :home/home))]]

       [tabbed-view {:tab-navs [[:profile (grab :team/profile)]
                                [:capacity (grab :team/capacity)]
                                [:performance (grab :team/performance)]
                                [:tasks (grab :team/tasks)]]
                     :views {:profile team-profile
                             :performance perfs
                             :capacity capacities
                             :tasks tasks}
                     :action-buttons [[#(show-confirm (grab :team/confirm-delete) [::events/delete-team @active-team])
                                       (str (grab :form/delete) " " (grab :team/title))
                                       "is-danger"]]
                     :title  (:name @active-team)}]])))