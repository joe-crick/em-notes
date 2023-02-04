(ns em-notes.lib.person-full-name)

(defn person-full-name [person]
  (let [{fname :first-name lname :last-name} person]
    (str fname " " lname)))