(ns em-notes.i18n.dev-translations)

(def translations-dict
  {:en
   {:missing ":en missing text"
    :home
    {:title "EM Notes"
     :create-person "Create a Person"
     :greet "Hello %1"
     :farewell "Goodbye %1"
     :foo "foo"
     :bar "bar"
     :bar-copy :en.example/bar ; Can alias entries
     :baz [:div "This is a **Hiccup** form"]}
    :form
    {:submit "Submit"
     :cancel "Cancel"}
    :person 
    {:title "Person"
     :first-name "First Name"
     :last-name "Last Name"
     :team "Team"
     :overview "Overview"
     :performance "Performance"
     :tasks "Tasks"}}})