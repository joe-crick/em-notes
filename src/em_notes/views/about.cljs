(ns em-notes.views.about 
   [re-frame.core :as re-frame]
   [em-notes.styles :as styles]
   [em-notes.events :as events]
   [em-notes.routes :as routes]
   [em-notes.subs :as subs]))

;; about

(defn about-panel []
  [:div
   [:h1 "This is the About Page."]

   [:div
    [:a {:on-click #(re-frame/dispatch [::events/navigate :home])}
     "go to Home Page"]]])

(defmethod routes/panels :about-panel [] [about-panel])