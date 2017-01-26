
(ns quatrefoil.comp.canvas
  (:require [quatrefoil.dsl.alias :refer [create-comp group box sphere]]))

(def comp-demo
  (create-comp
   :demo
   nil
   (fn []
     (fn [state mutate! instant]
       (group
        {}
        (box
         {:params {:width 16, :height 2, :depth 6},
          :material {:kind :line-basic, :color 0x808080, :opacity 0.6}}))))))

(def comp-canvas
  (create-comp
   :canvas
   {}
   (fn [store]
     (fn [state mutate! instant]
       (group
        {}
        (comp-demo)
        (sphere
         {:params {:radius 4}, :material {:kind :line-basic, :opacity 0.6, :color 0x505050}}))))))
