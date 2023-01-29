(ns em-notes.views.tasks.tasks
  (:require [em-notes.components.left-right-cols :refer [left-right]]
            [em-notes.events :as events]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.table-style :refer [table-style]]
            [em-notes.routing.nav :as nav]
            [em-notes.views.tasks.task :refer [task]]
            [re-frame.core :as rf]))

(defn tasks [active-person]
  (let [tasks (:tasks active-person)]
    (fn []
      [:div.container
       [left-right (fn [] [:h1 {:class "title"}
                           (grab :tasks/title)])
        (fn [] [:button {:class "button is-primary"
                         :on-click #(rf/dispatch [::events/set-modal {:title "Task"
                                                                      :content task
                                                                      :display "is-block"}])} (grab :tasks/create-task)])]

       [:table {:class (table-style)}
        [:thead
         [:tr
          [:th (grab :tasks/title)]
          [:th (grab :tasks/status)]
          [:th (grab :table/actions)]]]
        [:tbody
         (for [task tasks
               :let [[_ [task]] task]]
           ^{:key (random-uuid)} [:tr {:id (:task-id task)}
                                  [:td.name
                                   [:button {:class "button is-ghost"
                                             :on-click (fn []
                                                         (nav/go :task))} (:name task)]]
                                  [:td {:class "pt-4"} (:details task)]
                                  [:td [:button {:class "button is-danger is-small"
                                                 :on-click #(rf/dispatch [::events/delete-task [active-person task]])} (grab :form/delete)]]])]]])))