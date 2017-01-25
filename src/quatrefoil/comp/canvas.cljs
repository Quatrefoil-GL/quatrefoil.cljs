
(ns quatrefoil.comp.canvas
  (:require [quatrefoil.dsl.alias :refer [create-comp group cube]]))

(def comp-demo
  (create-comp :demo nil (fn [] (fn [state mutate! instant] (group {} (cube {}))))))

(def comp-canvas
  (create-comp :canvas {} (fn [store] (fn [state mutate! instant] (group {} (comp-demo))))))
