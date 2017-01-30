
(ns quatrefoil.updater.core )

(defn updater [store op op-data]
  (case op :add-task store :delete-task store :toggle-task store :edit-task store store))
