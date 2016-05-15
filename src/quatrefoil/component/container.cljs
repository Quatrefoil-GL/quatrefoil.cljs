
(ns quatrefoil.component.container
  (:require [quatrefoil.alias :refer [scene
                                      light box group create-comp]]))

(defn render [store]
  (fn [state mutate instant tick]
    (scene
      {}
      (light {:args [0xffffff 1.2 200], :attrs {:position [40 20 10]}})
      (box {:args [5 5 5], :material {:kind :lambert}}))))

(def comp-container (create-comp :container render))
