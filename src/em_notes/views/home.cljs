(ns em-notes.views.home
  (:require [em-notes.routing.nav :as nav]
            [em-notes.i18n.tr :refer [grab]]))

(defn home []
  [:section.mt-5
   [:div.container
    [:h1.title
     (grab :home/title)]

    [:div>button {:class "button is-link" :on-click #(nav/go :person)}
     (grab :home/create-person)]]])