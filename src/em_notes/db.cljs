(ns em-notes.db)

(def default-db
  {:teams {}
   :team {:name ""
          :people []
          :charter ""
          :values ""
          :data {:capacities []}}
   :active-team {:name ""
                 :people []
                 :team-id ""
                 :charter ""
                 :values ""
                 :data {:capacities []}}
   :people {}
   :person   {:first-name ""
              :last-name ""
              :team ""
              :profile {:attachment-style ""
                        :openness ""
                        :conscientiousness ""
                        :extroversion ""
                        :agreableness ""
                        :neuroticism ""
                        :three-hardest-things ""
                        :feedback {:feedback-medium ""
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
              :data {:perfs []
                     :one-on-ones []
                     :tasks []
                     :growth-metrics []}}
   :active-person   {:first-name ""
                     :last-name ""
                     :team ""
                     :profile {:attachment-style ""
                               :openness ""
                               :conscientiousness ""
                               :extroversion ""
                               :agreableness ""
                               :neuroticism ""
                               :three-hardest-things ""
                               :feedback {:feedback-medium ""
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
                     :data {:perfs []
                            :one-on-ones []
                            :tasks []
                            :growth-metrics []}}
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
   :default-growth-metric {:metric-id ""
                           :name ""
                           :details ""
                           :progress ""
                           :comments ""}
   :active-growth-metric {:metric-id ""
                          :name ""
                          :details ""
                          :progress ""
                          :comments ""}
   :default-one-on-one {:one-on-one-id ""
                        :week-of ""
                        :perf-improvement ""
                        :development ""
                        :next-steps ""
                        :notes ""}
   :active-one-on-one {:one-on-one-id ""
                       :week-of ""
                       :perf-improvement ""
                       :development ""
                       :next-steps ""
                       :notes ""}
   :default-perf {:perf-id ""
                  :week ""
                  :velocity ""
                  :prs ""
                  :collaboration ""
                  :avg-est-accuracy ""
                  :notes ""}
   :active-perf {:perf-id ""
                 :week ""
                 :velocity ""
                 :prs ""
                 :collaboration ""
                 :avg-est-accuracy ""
                 :notes ""}
   :default-capacity {:capacity-id ""
                      :week-of ""
                      :percent-capacity ""
                      :notes ""}
   :active-capacity {:capacity-id ""
                     :week-of ""
                     :percent-capacity ""
                     :notes ""}
   :toasts []
   :modal {}
   :init-queue []
   :active-context :people})
