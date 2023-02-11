(ns em-notes.views.tasks.tasks
  (:require [em-notes.components.card :refer [card]]
            [em-notes.components.left-right-cols :refer [left-right]]
            [em-notes.components.table-filter :refer [table-filter]]
            [em-notes.events :as events]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.css-cls :refer [css-cls]]
            [em-notes.lib.current-tab :refer [current-tab?]]
            [em-notes.lib.filter-map-on-prop :refer [filter-on-prop-str]]
            [em-notes.lib.local-state :refer [local-state]]
            [em-notes.lib.show-confirm :refer [show-confirm]]
            [em-notes.lib.show-modal :refer [show-modal]]
            [em-notes.lib.table-style :refer [table-style]]
            [em-notes.subs :as subs]
            [em-notes.views.tasks.task :as task]
            [re-frame.core :as rf]))

(defn task-list [completed?]
  (let [active-person (rf/subscribe [::subs/active-person])
        tasks (rf/subscribe [::subs/person-tasks completed?])
        [filter revise!] (local-state {:filter ""})]
    (fn []
      [:div.container
       [:h1 {:class (css-cls :subtitle)}
        (grab :tasks/title)]
       [:div.is-hidden (:full-name @active-person)]
       [table-filter filter revise!]
       [:table {:class (table-style)}
        [:thead
         [:tr
          [:th (grab :tasks/title)]
          [:th (grab :tasks/details)]
          [:th (grab :tasks/completed)]
          [:th (grab :table/actions)]]]
        [:tbody
         (for [task (filter-on-prop-str @tasks [:name] (:filter @filter))
               :let [task-id (:task-id task)
                     completed? (:completed task)]]
           ^{:key (random-uuid)} [:tr {:id task-id}
                                  [:td.name
                                   [:button {:class (css-cls :button :is-ghost)
                                             :on-click #(rf/dispatch [::events/edit-task [@active-person task task/task]])} (:name task)]]
                                  [:td {:class (css-cls ::pt-4)} (:details task)]
                                  [:td {:class (css-cls ::pt-4)} (str (:completed task))]
                                  [:td
                                   [:div {:class (css-cls :buttons :are-small :is-grouped)}
                                    [:button {:class (css-cls :button :is-info :is-fixed-100)
                                              :on-click  #(rf/dispatch [::events/toggle-task-status [@active-person task]])} (if completed? (grab :task/mark-incomplete) (grab :task/mark-complete))]
                                    [:button {:class (css-cls :button :is-danger :is-fixed-50)
                                              :on-click  #(show-confirm (grab :task/confirm-delete) [::events/delete-item [@active-person task :tasks :task-id]])} (grab :form/delete)]]]])]]])))

(defn open-tasks []
  [task-list true])

(defn closed-tasks []
  [task-list false])

(defn tasks []
  (let [[tab change-tab!] (local-state :open)
        views {:open open-tasks :closed closed-tasks}
        tab-navs [[:open (grab :tasks/open)]
                  [:closed (grab :tasks/closed)]]]
    (fn []
      [:div
       [:div {:class (css-cls :container :is-flex :is-justify-content-flex-end)}
        [:div {:class (css-cls :container :mb-1)}
         [:div.is-hidden @tab]
         [left-right (fn []
                       [:div
                        (for [[name label] tab-navs]
                          ^{:key (random-uuid)} [:button {:class (str "button " (current-tab? @tab name))
                                                          :data-name name
                                                          :on-click (fn []
                                                                      (change-tab! name))} label])])
          (fn [] [:button {:class (css-cls :button :is-primary)
                           :on-click #(show-modal (grab :task/title) task/task)} (grab :tasks/create-task)])]]]
       [card
        (fn [] [:div
                [(get views (keyword @tab) (fn [] [:div.container "Not Found"]))]])]]))
  )