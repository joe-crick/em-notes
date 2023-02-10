(ns em-notes.views.all-tasks
  (:require [em-notes.components.table-filter :refer [table-filter]]
            [em-notes.events :as events]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.bulma-cls :refer [bulma-cls]]
            [em-notes.lib.filter-map-on-prop :refer [filter-on-prop]]
            [em-notes.lib.local-state :refer [local-state]]
            [em-notes.lib.person.get-person-by-id :refer [get-person-by-id]]
            [em-notes.lib.show-confirm :refer [show-confirm]]
            [em-notes.lib.table-style :refer [table-style]]
            [em-notes.routing.nav :as nav]
            [em-notes.subs :as subs]
            [em-notes.views.tasks.task :as task]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(defn tasks []
  (r/create-class
   {:component-did-mount
    (fn []
      (rf/dispatch [::events/get-all-tasks]))
    :reagent-render
    (fn []
      (let [task-list (rf/subscribe [::subs/tasks])
            people (rf/subscribe [::subs/people])
            [filter revise!] (local-state {:filter ""})]
        (fn []
          [:div
           [:div {:class (bulma-cls :container :is-flex :is-justify-content-flex-end)}
            [:div>button {:class (bulma-cls :button :is-link) :on-click #(nav/go :task)}
             (grab :home/create-task)]]
           [:div {:class (bulma-cls :container) :style {:margin-top (bulma-cls :15px)}}
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
              (for [task (filter-on-prop (or @task-list []) [:name] (:filter @filter))
                    :let [task-id (:task-id task)
                          completed? (:completed task)
                          person-id (:person-id task)
                          person (get-person-by-id @people person-id)]]
                ^{:key (random-uuid)} [:tr {:id task-id}
                                       [:td.person (:full-name person)]
                                       [:td.name
                                        [:button {:class (bulma-cls :button :is-ghost)
                                                  :on-click #(rf/dispatch [::events/edit-task [person task task/task]])} (:name task)]]
                                       [:td {:class (bulma-cls :pt-4)} (:details task)]
                                       [:td {:class (bulma-cls :pt-4)} (str (:completed task))]
                                       [:td
                                        [:div {:class (bulma-cls :buttons :are-small :is-grouped)}
                                         [:button {:class (bulma-cls :button :is-info :is-fixed-100)
                                                   :on-click  #(rf/dispatch [::events/toggle-task-all-status [person task]])} (if completed? (grab :task/mark-incomplete) (grab :task/mark-complete))]
                                         [:button {:class (bulma-cls :button :is-danger :is-fixed-50)
                                                   :on-click  #(show-confirm (grab :task/confirm-delete) [::events/delete-task-all [person task]])} (grab :form/delete)]]]])]]]])))}))


