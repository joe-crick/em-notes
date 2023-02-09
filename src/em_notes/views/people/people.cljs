(ns em-notes.views.people.people
  (:require [em-notes.components.table-filter :refer [table-filter]]
            [em-notes.events :as events]
            [em-notes.i18n.tr :refer [grab]]
            [em-notes.lib.bulma-cls :refer [bulma-cls]]
            [em-notes.lib.filter-map-on-prop :refer [fiilter-map-on-prop]]
            [em-notes.lib.local-state :refer [local-state]]
            [em-notes.lib.table-style :refer [table-style]]
            [em-notes.subs :as subs]
            [re-frame.core :as re-frame]))



(defn people []
  ;; setup local state
  (let [people (re-frame/subscribe [::subs/people])
        [l-people revise!] (local-state {:people @people :filter ""})]
    ;; required when local state is used, because we need to return a render function
    (fn []
      [:div
       [:div {:class (bulma-cls :container :is-flex :is-justify-content-flex-end)}
        [:div>button {:class (bulma-cls :button :is-link) :on-click #(re-frame/dispatch [::events/create-person])}
         (grab :home/create-person)]]
       [:div.container {:style {:margin-top "15px"}}
        [table-filter l-people revise!]
        [:table {:class (table-style)}
         [:thead
          [:tr
           [:th (grab :person/title)]
           [:th (grab :person/team)]]]
         [:tbody
          (for [person (fiilter-map-on-prop (or (:people @l-people) []) [:full-name] (:filter @l-people))
                :let [person-id (:person-id person)
                      person-name (:full-name person)]]
            ^{:key (random-uuid)} [:tr {:id person-id}
                                   [:td.name
                                    [:button {:class (bulma-cls :button :is-ghost)
                                              :on-click #(re-frame/dispatch [::events/show-person person-id])} person-name]]
                                   [:td {:class (bulma-cls :team :pt-4)} (:team person)]])]]]])))

