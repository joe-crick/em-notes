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
              :tasks []
              :growth-metrics []}
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
                     :tasks []
                     :growth-metrics []}
   :default-modal {:title ""
                   :content nil
                   :footer []
                   :display "is-hidden"}
   :default-confirm {:title ""
                     :content nil
                     :footer []
                     :display "is-hidden"}
   :default-task {:task-id ""
                  :name ""
                  :details ""
                  :due-date ""
                  :completed false}
   :active-task {:task-id ""
                 :name ""
                 :details ""
                 :due-date ""
                 :completed false}
   :default-grwoth-metric {:metric-id ""
                           :name ""
                           :details ""
                           :progress ""
                           :comments ""}
   :active-growth-metric {:metric-id ""
                          :name ""
                          :details ""
                          :progress ""
                          :comments ""}
   :toasts []
   :modal {}
   :init-queue []})
