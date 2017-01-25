
(ns quatrefoil.comp.canvas
  (:require [quatrefoil.dsl.alias :refer [create-comp scene group cube]]))

(def comp-demo
  (create-comp :demo nil (fn [] (fn [state mutate! instant] (group {} (cube {}))))))

(def comp-canvas
  (create-comp :scene {} (fn [store] (fn [state mutate! instant] (scene {} (comp-demo))))))
