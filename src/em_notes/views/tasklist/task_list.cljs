(ns em-notes.views.tasklist.task-list
  (:require [em-notes.components.table-filter :refer [table-filter]]
            [em-notes.events :as events]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.css-cls :refer [css-cls]]
            [em-notes.lib.filter-map-on-prop :refer [filter-on-prop-str]]
            [em-notes.lib.person.get-person-by-id :refer [get-person-by-id]]
            [em-notes.lib.show-confirm :refer [show-confirm]]
            [em-notes.lib.table-style :refer [table-style]]
            [em-notes.lib.task.get-task-entity-id :refer [get-task-entity-id]]
            [em-notes.lib.task.get-task-type :refer [get-task-type]]
            [em-notes.lib.team.get-team-by-id :refer [get-team-by-id]]
            [em-notes.routing.nav :as nav]
            [em-notes.subs :as subs]
            [em-notes.views.tasks.task :as task]
            [re-frame.core :as rf]))

(defn task-list [filter revise! completed?]
  (let [task-list (rf/subscribe [::subs/tasks completed?])
        people (rf/subscribe [::subs/people])
        teams (rf/subscribe [::subs/teams])]
    (fn []
      [:div {:class (css-cls :container) :style {:margin-top (css-cls :15px)}}
       [table-filter filter revise!]
       [:table {:class (table-style)}
        [:thead
         [:tr
          [:th (grab :tasks/person)]
          [:th (grab :tasks/title)]
          [:th (grab :tasks/details)]
          [:th (grab :tasks/completed)]
          [:th (grab :table/actions)]]]
        [:tbody
         (for [task (filter-on-prop-str (or @task-list []) [:name] (:filter @filter))
               :let [task-id (:task-id task)
                     completed? (:completed task)
                     owner-id (:owner-id task)
                     ;; is-team is a property that only exists on a task returned from the all-tasks api
                     team? (:is-team task)
                     entity (if team?
                              (get-team-by-id @teams owner-id)
                              (get-person-by-id @people owner-id))
                     entity-key (if team? :name :full-name)]]
           ^{:key (random-uuid)} [:tr {:id task-id}
                                  [:td.name
                                   [:button {:class (css-cls :button :is-ghost)
                                             :on-click (if team? 
                                                         #(nav/go :team (str "id=" owner-id))
                                                         #(rf/dispatch [::events/show-person owner-id]))} (entity-key entity)]]
                                  [:td.name
                                   [:button {:class (css-cls :button :is-ghost)
                                             :on-click #(rf/dispatch [::events/edit-task [(get-task-entity-id entity) task task/task]])} (:name task)]]
                                  [:td {:class (css-cls :pt-4)} (:details task)]
                                  [:td {:class (css-cls :pt-4)} (str (:completed task))]
                                  [:td
                                   [:div {:class (css-cls :buttons :are-small :is-grouped)}
                                    [:button {:class (css-cls :button :is-info :is-fixed-100)
                                              :on-click  #(rf/dispatch [::events/toggle-task-all-status [entity task]])} (if completed? (grab :task/mark-incomplete) (grab :task/mark-complete))]
                                    [:button {:class (css-cls :button :is-danger :is-fixed-50)
                                              :on-click  #(show-confirm (grab :task/confirm-delete) [::events/delete-task-all [entity task]])} (grab :form/delete)]]]])]]])))

(defn open-tasks [filter revise!]
  [task-list filter revise! true])

(defn closed-tasks [filter revise!]
  [task-list filter revise! false])