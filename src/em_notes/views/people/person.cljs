(ns em-notes.views.people.person
  (:require
   [em-notes.components.tabbed-view :refer [tabbed-view]]
   [em-notes.events :as events]
   [em-notes.i18n.tr :refer [grab]]
   [em-notes.lib.show-confirm :refer [show-confirm]]
   [em-notes.routing.nav :as nav]
   [em-notes.subs :as subs]
   [em-notes.views.people.growth.metrics :refer [metrics]]
   [em-notes.views.people.person-profile.profile-form :refer [profile]]
   [em-notes.views.performance.perfs :refer [perfs]]
   [em-notes.views.people.one-on-ones.one-on-ones :refer [one-on-ones]]
   [em-notes.views.tasks.tasks :refer [tasks]]
   [re-frame.core :as rf]))


(defn person []
  (let [active-person (rf/subscribe [::subs/active-person])] 
    (fn []
      [:section {:style {:margin-top "-40px"}}
       [:div {:class "container"}
        [:button {:class "button is-ghost mt-5" :on-click #(nav/go :home)} (str "< " (grab :home/home))]]
       [tabbed-view {:tab-navs [[:profile (grab :person/profile)]
                                [:performance (grab :person/performance)]
                                [:career-growth (grab :person/career-growth)]
                                [:tasks (grab :person/tasks)]
                                [:one-on-ones (grab :person/one-on-ones)]]
                     :views {:profile profile
                             :performance perfs
                             :career-growth metrics
                             :tasks tasks
                             :one-on-ones one-on-ones}
                     :action-buttons [[#(show-confirm (grab :person/confirm-delete) [::events/delete-person @active-person])
                                       (str (grab :form/delete) " " (grab :person/title))
                                       "is-danger"]]
                     :title (:full-name @active-person)}]])))