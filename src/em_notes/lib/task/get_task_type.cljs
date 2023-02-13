(ns em-notes.lib.task.get-task-type)

(defn get-task-type [task db]
  (let [owner-id (keyword (:owner-id task))]
    (if (get-in db [:people owner-id]) "person" "team")))