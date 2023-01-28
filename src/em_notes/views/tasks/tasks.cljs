(ns em-notes.views.tasks.tasks
  (:require
   [em-notes.routing.nav :as nav]
   [em-notes.i18n.tr :refer [grab]]
   [em-notes.events :as events]
   [re-frame.core :as rf]
   [em-notes.components.form-footer :refer [form-footer]]
   [em-notes.views.tasks.task :refer [task]]
   [em-notes.lib.table-style :refer [table-style]]
   [em-notes.components.left-right-cols :refer [left-right]]
   [em-notes.lib.nab :refer [nab]]))

(defn tasks [tasks]
  [:div.container
   [left-right (fn [] [:h1 {:class "title"}
                       (grab :tasks/title)])
    (fn [] [:button {:class "button is-primary"
                     :on-click #(rf/dispatch [::events/set-modal {:title "Task"
                                                                  :content task
                                                                  :footer [form-footer (fn [] (rf/dispatch [::events/set-active-task []]))]
                                                                  :display "is-block"}])} (grab :tasks/create-task)])]

   [:table {:class (table-style)}
    [:thead
     [:tr
      [:th (grab :tasks/title)]
      [:th (grab :tasks/status)]
      [:th (grab :table/actions)]]]
    [:tbody
     (for [task tasks
           :let [task-id (:task-id task)]]
       ^{:key (random-uuid)} [:tr {:id task-id}
                              [:td.name
                               [:button {:class "button is-ghost"
                                         :on-click (fn []
                                                     (nav/go :task))} (:title task)]]
                              [:td.team (nab :team task)]])]]])