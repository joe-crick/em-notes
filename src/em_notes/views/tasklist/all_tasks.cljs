(ns em-notes.views.tasklist.all-tasks
  (:require [em-notes.components.card :refer [card]]
            [em-notes.components.left-right-cols :refer [left-right]]
            [em-notes.events :as events]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.css-cls :refer [css-cls]]
            [em-notes.lib.current-tab :refer [current-tab?]]
            [em-notes.lib.local-state :refer [local-state]]
            [em-notes.lib.show-modal :refer [show-modal]]
            [em-notes.views.tasks.task :as task]
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
      (let [
            [filter revise!] (local-state {:filter ""})
            [tab change-tab!] (local-state :open)
            views {:open open-tasks :closed closed-tasks}
            tab-navs [[:open (grab :tasks/open)]
                      [:closed (grab :tasks/closed)]]]
        (fn []
          [:div
           [:div {:class (css-cls :container :is-flex :is-justify-content-flex-end)}
            [:div {:class (css-cls :container :mb-1)}
             [:div.is-hidden @tab]]]

           [:div
            (for [[name label] tab-navs]
              ^{:key (random-uuid)} [:button {:class (str "button " (current-tab? @tab name))
                                              :data-name name
                                              :on-click (fn []
                                                          (change-tab! name))} label])]
           
           [card
            (fn [] [:div
                    [(get views (keyword @tab) (fn [] [:div.container "Not Found"])) filter revise!]])]])))}))


