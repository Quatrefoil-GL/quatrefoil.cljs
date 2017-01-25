
(ns quatrefoil.comp.canvas (:require [quatrefoil.dsl.alias :refer [create-comp scene]]))

(def comp-canvas
  (create-comp :scene {} (fn [store] (fn [state mutate! instant] (scene {})))))
