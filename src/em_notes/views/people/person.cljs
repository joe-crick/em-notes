(ns em-notes.views.people.person
  (:require [em-notes.components.card :refer [card]]
            [em-notes.components.left-right-cols :refer [left-right]]
            [em-notes.events :as events]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.css-cls :refer [css-cls]]
            [em-notes.lib.current-tab :refer [current-tab?]]
            [em-notes.lib.local-state :refer [local-state]]
            [em-notes.lib.show-confirm :refer [show-confirm]]
            [em-notes.routing.nav :as nav]
            [em-notes.subs :as subs]
            [em-notes.views.people.growth.metrics :refer [metrics]]
            [em-notes.views.people.one-on-ones.one-on-ones :refer [one-on-ones]]
            [em-notes.views.people.person-profile.profile-form :refer [profile]]
            [em-notes.views.people.performance.perfs :refer [perfs]]
            [em-notes.views.tasks.tasks :refer [tasks]]
            [re-frame.core :as rf]))

(defn person []
  (let [active-person (rf/subscribe [::subs/active-person])
        [tab change-tab!] (local-state :profile)
        tab-navs [[:profile (grab :person/profile)]
                  [:performance (grab :person/performance)]
                  [:career-growth (grab :person/career-growth)]
                  [:tasks (grab :person/tasks)]
                  [:one-on-ones (grab :person/one-on-ones)]]
        views {:profile profile
               :performance perfs
               :career-growth metrics
               :tasks tasks
               :one-on-ones one-on-ones}
        action-buttons [[#(show-confirm (grab :person/confirm-delete) [::events/delete-person @active-person])
                         (str (grab :form/delete) " " (grab :person/title))
                         "is-danger"]]]
    (fn []
      [:section {:style {:margin-top "-40px"}}
       [:div {:class (css-cls :container)}
        [:button {:class (css-cls :button :is-ghost :mt-5) :on-click #(nav/go :home)} (str "< " (grab :home/home))]]
       [:section

     ;; show in view to make atom reactive
        [:div.is-hidden (str @tab)]

     ;; Tabs
        [:div {:class (css-cls :container :mb-1)}
         [left-right (fn []) (fn []
                               [:div
                                (for [[name label] tab-navs]
                                  ^{:key (random-uuid)} [:button {:class (str "button " (current-tab? @tab name))
                                                                  :data-name name
                                                                  :on-click (fn []
                                                                              (change-tab! name))} label])])]]
     ;; Actions
        [:div.container
         [left-right (fn [])
          (fn []
            [:div.container
             (for [[on-click label btn-type] action-buttons]
               ^{:key (random-uuid)} [:button {:class (str "button :mt-5 mb-3 " btn-type) :on-click on-click} label])])]]

     ;; Body
        [card
         (fn [] [:div
                 [:h1 {:class (css-cls :title)} (:full-name @active-person)]
                 [(get views @tab (fn [] [:div.container "Not Found"]))]])]]])))