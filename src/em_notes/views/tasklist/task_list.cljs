(ns em-notes.views.tasklist.task-list
  (:require [em-notes.components.table-filter :refer [table-filter]]
            [em-notes.events :as events]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.css-cls :refer [css-cls]]
            [em-notes.lib.filter-map-on-prop :refer [filter-on-prop-str]]
            [em-notes.lib.person.get-person-by-id :refer [get-person-by-id]]
            [em-notes.lib.show-confirm :refer [show-confirm]]
            [em-notes.lib.table-style :refer [table-style]]
            [em-notes.views.tasks.task :as task]
            [em-notes.subs :as subs]
            [re-frame.core :as rf]))

(defn task-list [people filter revise! completed?]
  (let [task-list (rf/subscribe [::subs/tasks completed?])]
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
                     person-id (:person-id task)
                     person (get-person-by-id @people person-id)]]
           ^{:key (random-uuid)} [:tr {:id task-id}
                                  [:td.name
                                   [:button {:class (css-cls :button :is-ghost)
                                             :on-click #(rf/dispatch [::events/show-person person-id])} (:full-name person)]]
                                  [:td.name
                                   [:button {:class (css-cls :button :is-ghost)
                                             :on-click #(rf/dispatch [::events/edit-task [person task task/task]])} (:name task)]]
                                  [:td {:class (css-cls :pt-4)} (:details task)]
                                  [:td {:class (css-cls :pt-4)} (str (:completed task))]
                                  [:td
                                   [:div {:class (css-cls :buttons :are-small :is-grouped)}
                                    [:button {:class (css-cls :button :is-info :is-fixed-100)
                                              :on-click  #(rf/dispatch [::events/toggle-task-all-status [person task]])} (if completed? (grab :task/mark-incomplete) (grab :task/mark-complete))]
                                    [:button {:class (css-cls :button :is-danger :is-fixed-50)
                                              :on-click  #(show-confirm (grab :task/confirm-delete) [::events/delete-task-all [person task]])} (grab :form/delete)]]]])]]])))

(defn open-tasks [people filter revise!]
  [task-list people filter revise! true])

(defn closed-tasks [people filter revise!]
  [task-list people filter revise! false])