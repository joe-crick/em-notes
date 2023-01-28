(ns em-notes.views.people.people
    (:require
     [re-frame.core :as re-frame]
     [em-notes.routing.nav :as nav]
     [em-notes.subs :as subs]
     [em-notes.i18n.tr :refer [grab]]
     [em-notes.lib.lower-case :refer [lower-case]] 
     [em-notes.lib.nab :refer [nab]]))

(defn people []
  ;; setup local state
  (let [people (re-frame/subscribe [::subs/people])]
    ;; required when local state is used, because we need to return a render function
    (fn []
      [:section
       [:div {:class "container is-flex is-justify-content-flex-end"}
        [:div>button {:class "button is-link" :on-click #(nav/go :person)}
         (grab :home/create-person)]]
       [:div.container
        [:table {:class "table is-striped is-hoverable"}
         [:thead
          [:tr
           [:th (grab :person/title)]
           [:th (grab :person/team)]]]
         [:tbody
          (for [rec @people
                :let [person (second (clj->js rec))
                      person-id (lower-case (str (nab :first-name person) "-" (nab :last-name person)))
                      person-name (str (nab :first-name person) " " (nab :last-name person))]]
            ^{:key (random-uuid)} [:tr {:id person-id}
                                   [:td {:class "name"}
                                    [:button {:class "button is-ghost"
                                              :on-click #(nav/go :person (str "id=" person-id))} person-name]]
                                   [:td {:class "team has-text-centered pt-4"} (nab :team person)]])]]]])))

