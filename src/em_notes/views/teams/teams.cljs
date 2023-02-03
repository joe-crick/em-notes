(ns em-notes.views.teams.teams
    (:require
     [re-frame.core :as re-frame]
     [em-notes.routing.nav :as nav]
     [em-notes.subs :as subs]
     [em-notes.i18n.tr :refer [grab]]
     [em-notes.lib.table-style :refer [table-style]]))

(defn teams []
  ;; setup local state
  (let [teams (re-frame/subscribe [::subs/teams])]
    ;; required when local state is used, because we need to return a render function
    (fn []
      [:div
       [:div {:class "container is-flex is-justify-content-flex-end"}
        [:div>button {:class "button is-link" :on-click #(nav/go :team)}
         (grab :home/create-team)]]
       [:div {:class "container" :style {:margin-top "15px"}}
        [:table {:class (table-style)}
         [:thead
          [:tr
           [:th (grab :team/title)]]]
         [:tbody
          (for [[_ team] @teams
                :let [team-name (:name team)
                      team-id (:team-id team)]]
            (do
              ^{:key (random-uuid)} [:tr {:id team-id}
                                   [:td {:class "name"}
                                    [:button {:class "button is-ghost"
                                              :on-click #(nav/go :team (str "id=" team-id))} team-name]]]))]]]])))

