(ns em-notes.db)

(def default-db
  {:teams [{:name "Test"
            :people [{:first-name "Bob" :last-name "Chun"}
                     {:first-name "Chen" :last-name "Swift"}]}
           {:name "Example"
            :people [{:first-name "Callan" :last-name "Danilovich"}
                     {:first-name "Ron" :last-name "Granger"}
                     {:first-name "Hardy" :last-name "Thomas"}]}]
   :person
   {:first-name ""
    :last-name ""
    :team ""
    :profile
    {:feedback
     {:feedback-medium ""
      :when-receive-feedback ""
      :how-receive-recognition ""
      :what-recognition-wanted ""}
     :grumpy
     {:what-makes ""
      :how-to-know ""
      :how-to-help ""}
     :support
     {:manager ""
      :team ""
      :peers-outside ""}
     :growth
     {:current-goals ""
      :what-doing-now ""
      :what-need-to-do ""}}}})
