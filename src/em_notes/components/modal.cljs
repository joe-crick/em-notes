(ns em-notes.components.modal)

(defn modal [modalClassName title content]
  [:div
   {:class (str "modal " modalClassName)}
   [:div {:class "modal-background"}]
   [:div
    {:class "modal-card"}
    [:header
     {:class "modal-card-head"}
     [:p {:class "modal-card-title"} title]
     [:button {:class "delete", :aria-label "close"}]]
    [:section {:class "modal-card-body"} (if content [content] "")]]])