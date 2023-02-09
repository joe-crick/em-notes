(ns em-notes.views.teams.teams
    (:require [em-notes.components.table-filter :refer [table-filter]]
              [em-notes.i18n.tr :refer [grab]]
              [em-notes.lib.filter-map-on-prop :refer [filter-map-on-prop]]
              [em-notes.lib.local-state :refer [local-state]]
              [em-notes.lib.table-style :refer [table-style]]
              [em-notes.routing.nav :as nav]
              [em-notes.subs :as subs]
              [re-frame.core :as re-frame]))

(defn teams []
  ;; setup local state
  (let [teams (re-frame/subscribe [::subs/teams])
        [filter revise!] (local-state {:filter ""})]
    ;; required when local state is used, because we need to return a render function
    (fn []
      [:div
       [:div {:class "container is-flex is-justify-content-flex-end"}
        [:div>button {:class "button is-link" :on-click #(nav/go :team)}
         (grab :home/create-team)]]
       [:div {:class "container" :style {:margin-top "15px"}}
        [table-filter filter revise!]
        [:table {:class (table-style)}
         [:thead
          [:tr
           [:th (grab :team/title)]]]
         [:tbody
          (for [team (filter-map-on-prop (or @teams []) [:name] (:filter @filter))
                :let [team-name (:name team)
                      team-id (:team-id team)]]
            ^{:key (random-uuid)} [:tr {:id team-id}
                                   [:td {:class "name"}
                                    [:button {:class "button is-ghost"
                                              :on-click #(nav/go :team (str "id=" team-id))} team-name]]])]]]])))

