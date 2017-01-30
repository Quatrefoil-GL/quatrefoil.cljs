
(ns quatrefoil.updater.core )

(defn updater [store op op-data]
  (case op
    :add-task
      (update
       store
       :tasks
       (fn [tasks]
         (let [id (js/Date.now)] (assoc tasks id {:id id, :text op-data, :done? false}))))
    :delete-task (update store :tasks (fn [tasks] (dissoc tasks op-data)))
    :toggle-task (update store :tasks (fn [tasks] (update-in tasks [op-data :done?] not)))
    :edit-task
      (update
       store
       :tasks
       (fn [tasks] (assoc-in tasks [(first op-data) :text] (last op-data))))
    store))
