(ns em-notes.i18n.dev-translations)

(def translations-dict
  {:en
   {:missing ":en missing text"
    :home
    {:title "EM Notes"
     :home "Home"
     :create-person "Create a Person"
     :create-team "Create a Team"
     :greet "Hello %1"
     :farewell "Goodbye %1"
     :foo "foo"
     :bar "bar"
     :bar-copy :en.example/bar ; Can alias entries
     :baz [:div "This is a **Hiccup** form"]}
    :form
    {:submit "Save"
     :cancel "Cancel"
     :saved "Saved!"
     :deleted "Deleted!"
     :delete "Delete"
     :del "del"}
    :perfs
    {:create-perf "Create Perf"
     :estimation "Est. Acc."}
    :perf
    {:title "Performance"
     :week "Week of"
     :velocity "Velocity"
     :prs "PRs"
     :collaboration "Collaboration"
     :avg-est-accuracy "Avg. Estimation Accuracy"
     :notes "Notes"
     :confirm-delete "Are you certain you want to delete this perf record?"}
    :tasks
    {:title "Tasks"
     :details "Details"
     :due-date "Due Date"
     :completed "Completed"
     :name "Name"
     :create-task "Create Task"}
    :task {:title "Task"
           :name "Name"
           :details "Details"
           :due-date "Due Date"
           :completed "Completed"
           :confirm-delete "Are you certain you want to delete this task?"
           :mark-complete "Complete"
           :mark-incomplete "Open"
           :true "true"
           :false "false"}
    :growth-metrics {:title "Career Growth"
                     :name "Name"
                     :details "Details"
                     :progress "Progress"
                     :create-metric "Create Metric"
                     :confirm-delete "Are you certain you want to delete this metric?"}
    :growth-metric {:title "Growth Metric"
                    :name "Name"
                    :details "Details"
                    :progress "Progress"
                    :comments "Comments"
                    :begin "Begin"
                    :in-progress "In progress"
                    :achieved "Achieved"}
    :confirm {:title "Confirm"
              :yes "Yes"}
    :teams
    {:title "Teams"}
    :team {:title "Team"
           :profile "Profile"
           :capacity "Capacity"
           :performance "Performance"
           :tasks "Tasks"
           :name "Name"
           :people "People"
           :charter "Charter"
           :values "Values"}
    :people
    {:title "People"}
    :table {:actions "Actions"}
    :person
    {:title "Person"
     :details "Details"
     :feedback "Feedback"
     :feedback-medium "What feedback medium to use"
     :when-receive-feedback "When to receive feedback"
     :how-receive-recognition "Give recognition"
     :what-rewards-wanted "Good rewards"
     :mood "Mood"
     :what-makes-grumpy "What makes them grumpy"
     :how-to-know-grumpy "How to know if grumpy"
     :how-to-help-grumpy "How to help if grumpy"
     :support "Support"
     :support-manager "What they need from manager"
     :support-team "What they need from team"
     :peers-outside "What they need from outside peers"
     :growth "Growth"
     :current-goals "Current goals"
     :what-doing-now "What they're doing now"
     :what-need-to-do "What they need to do"
     :first-name "First Name"
     :last-name "Last Name"
     :team "Team"
     :profile "Profile"
     :performance "Performance"
     :career-growth "Career Growth"
     :tasks "Tasks"
     :confirm-delete "Are you certain you want to delete this person?"}}})