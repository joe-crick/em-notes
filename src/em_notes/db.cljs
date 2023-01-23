(ns em-notes.db)

(def default-db
  {:team {:name ""
          :people {}}
   ; store all the people in one, map, then map over people to get people per team
   :people {}
   :person
   {:first-name ""
    :last-name ""
    :team ""
    :profile
    {:feedback
     {:feedback-medium ""
      :when-receive-feedback ""
      :how-receive-recognition ""
      :what-rewards-wanted ""}
     :mood
     {:what-makes-grumpy ""
      :how-to-know-grumpy ""
      :how-to-help-grumpy ""}
     :support
     {:manager ""
      :team ""
      :peers-outside ""}
     :growth
     {:current-goals ""
      :what-doing-now ""
      :what-need-to-do ""}}}
   :active-person
   {:first-name ""
    :last-name ""
    :team ""
    :profile
    {:feedback
     {:feedback-medium ""
      :when-receive-feedback ""
      :how-receive-recognition ""
      :what-rewards-wanted ""}
     :mood
     {:what-makes-grumpy ""
      :how-to-know-grumpy ""
      :how-to-help-grumpy ""}
     :support
     {:manager ""
      :team ""
      :peers-outside ""}
     :growth
     {:current-goals ""
      :what-doing-now ""
      :what-need-to-do ""}}}})
