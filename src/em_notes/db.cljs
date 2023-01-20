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
    :team ""}})
