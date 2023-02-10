(ns em-notes.views.tasklist.all-tasks
  (:require [em-notes.components.card :refer [card]]
            [em-notes.components.left-right-cols :refer [left-right]]
            [em-notes.events :as events]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.bulma-cls :refer [bulma-cls]]
            [em-notes.lib.current-tab :refer [current-tab?]]
            [em-notes.lib.local-state :refer [local-state]]
            [em-notes.routing.nav :as nav]
            [em-notes.subs :as subs]
            [em-notes.views.tasklist.task-list :refer [closed-tasks open-tasks]]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(defn tasks []
  (r/create-class
   {:component-did-mount
    (fn []
      (rf/dispatch [::events/get-all-tasks]))
    :reagent-render
    (fn []
      (let [people (rf/subscribe [::subs/people])
            [filter revise!] (local-state {:filter ""})
            [tab change-tab!] (local-state :open)
            views {:open open-tasks :closed closed-tasks}
            tab-navs [[:open (grab :tasks/open)]
                      [:closed (grab :tasks/closed)]]]
        (fn []
          [:div
           [:div {:class (bulma-cls :container :is-flex :is-justify-content-flex-end)}
            [:div {:class "container mb-1"}
             [:div.is-hidden @tab]]]
           [left-right (fn []
                         [:div
                          (for [[name label] tab-navs]
                            ^{:key (random-uuid)} [:button {:class (str "button " (current-tab? @tab name))
                                                            :data-name name
                                                            :on-click (fn []
                                                                        (change-tab! name))} label])])
            (fn [] [:div>button {:class (bulma-cls :button :is-link) :on-click #(nav/go :task)}
                    (grab :tasks/create-task)])]
           [card
            (fn [] [:div
                    [(get views (keyword @tab) (fn [] [:div.container "Not Found"])) people filter revise!]])]])))}))


