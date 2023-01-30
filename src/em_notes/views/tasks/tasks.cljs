(ns em-notes.views.tasks.tasks
  (:require [em-notes.components.left-right-cols :refer [left-right]]
            [em-notes.events :as events]
            [em-notes.subs :as subs]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.show-confirm :refer [show-confirm]]
            [em-notes.lib.show-modal :refer [show-modal]]
            [em-notes.lib.table-style :refer [table-style]] 
            [em-notes.views.tasks.task :as task]
            [re-frame.core :as rf]))

(defn tasks []
  (let [active-person (rf/subscribe [::subs/active-person])
        tasks (:tasks @active-person)] 
    (fn []
      [:div.container
       [left-right (fn [] [:h1 {:class "title"}
                           (grab :tasks/title)])
        (fn [] [:button {:class "button is-primary"
                         :on-click #(show-modal (grab :task/title) task/task)} (grab :tasks/create-task)])]
       [:table {:class (table-style)}
        [:thead
         [:tr
          [:th (grab :tasks/title)]
          [:th (grab :tasks/details)]
          [:th (grab :tasks/status)]
          [:th (grab :table/actions)]]]
        [:tbody
         (for [task tasks
               :let [[_ task] task
                     task-id (:task-id task)
                     completed? (:completed task)]]
           ^{:key (random-uuid)} [:tr {:id task-id}
                                  [:td.name
                                   [:button {:class "button is-ghost"
                                             :on-click #(rf/dispatch [::events/edit-task [@active-person task task/task]])} (:name task)]]
                                  [:td {:class "pt-4"} (:details task)]
                                  [:td {:class "pt-4"} (str (:completed task))]
                                  [:td  
                                   [:div {:class "buttons are-small is-grouped"}
                                    [:button {:class "button is-info is-fixed-100"
                                              :on-click  #(rf/dispatch [::events/toggle-task-status [@active-person task]])} (if completed? (grab :task/mark-incomplete) (grab :task/mark-complete))]
                                    [:button {:class "button is-danger is-fixed-50"
                                              :on-click  #(show-confirm (grab :task/confirm-delete) [::events/delete-task [@active-person task]])} (grab :form/delete)]]]])]]])))