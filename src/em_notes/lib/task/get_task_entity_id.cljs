(ns em-notes.lib.task.get-task-entity-id)

(defn get-task-entity-id [entity]
  (if (nil? (:person-id entity)) (:team-id entity) (:person-id entity)))