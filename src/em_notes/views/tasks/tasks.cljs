(ns em-notes.views.tasks.tasks
  (:require      [re-frame.core :as re-frame]
                 [em-notes.routing.nav :as nav]
                 [em-notes.events :as events]
                 [em-notes.i18n.tr :refer [grab]]
                 [em-notes.lib.nab :refer [nab]]))

(defn tasks [tasks]
  [:div.container
   [:table {:class "table is-striped is-hoverable"}
    [:thead
     [:tr
      [:th (grab :tasks/title)]
      [:th (grab :tasks/status)]]]
    [:tbody
     (for [task @tasks
           :let [task-id (:task-id task)]]
       ^{:key task-id} [:tr {:id task-id}
                          [:td.name
                           [:button {:class "button is-ghost"
                                     :on-click (fn []
                                                 (re-frame/dispatch-sync [::events/set-active-task task-id])
                                                 (nav/go :task))} (:title task)]]
                          [:td.team (nab :team task)]])]]])