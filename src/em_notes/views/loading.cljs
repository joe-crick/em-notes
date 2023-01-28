(ns em-notes.views.loading)

(defn loading-splash []
  [:div.container
   [:h1 {:class "title is-1"} "Loading..."]
   [:progress
    {:class "progress is-success", :value "90", :max "100"}
    "60%"]])