(ns em-notes.views.people.people
    (:require
     [re-frame.core :as re-frame]
     [em-notes.routing.nav :as nav]
     [em-notes.subs :as subs]
     [em-notes.i18n.tr :refer [grab]]
     [em-notes.lib.table-style :refer [table-style]]))

(defn people []
  ;; setup local state
  (let [people (re-frame/subscribe [::subs/people])]
    ;; required when local state is used, because we need to return a render function
    (fn []
      [:div
       [:div {:class "container is-flex is-justify-content-flex-end"}
        [:div>button {:class "button is-link" :on-click #(nav/go :person)}
         (grab :home/create-person)]]
       [:div {:class "container" :style {:margin-top "15px"}}
        [:table {:class (table-style)}
         [:thead
          [:tr
           [:th (grab :person/title)]
           [:th (grab :person/team)]]]
         [:tbody
          (for [[_ person] @people
                :let [person-id (:person-id person)
                      person-name (:full-name person)]]
            ^{:key (random-uuid)} [:tr {:id person-id}
                                   [:td {:class "name"}
                                    [:button {:class "button is-ghost"
                                              :on-click #(nav/go :person (str "id=" person-id))} person-name]]
                                   [:td {:class "team pt-4"} (:team person)]])]]]])))

