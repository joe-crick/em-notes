(ns em-notes.db)

(def default-db
  {:people {}
   :person   {:first-name ""
              :last-name ""
              :team ""
              :profile
              {:feedback {:feedback-medium ""
                          :when-receive-feedback ""
                          :how-receive-recognition ""
                          :what-rewards-wanted ""}
               :mood {:what-makes-grumpy ""
                      :how-to-know-grumpy ""
                      :how-to-help-grumpy ""}
               :support {:manager ""
                         :team ""
                         :peers-outside ""}
               :growth  {:current-goals ""
                         :what-doing-now ""
                         :what-need-to-do ""}}
              :tasks []}
   :active-person   {:first-name ""
                     :last-name ""
                     :team ""
                     :profile
                     {:feedback {:feedback-medium ""
                                 :when-receive-feedback ""
                                 :how-receive-recognition ""
                                 :what-rewards-wanted ""}
                      :mood {:what-makes-grumpy ""
                             :how-to-know-grumpy ""
                             :how-to-help-grumpy ""}
                      :support {:manager ""
                                :team ""
                                :peers-outside ""}
                      :growth {:current-goals ""
                               :what-doing-now ""
                               :what-need-to-do ""}}
                     :tasks []}
   :active-task {:name ""
                 :details ""
                 :due-date ""
                 :completed false}})
