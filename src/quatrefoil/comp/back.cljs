
(ns quatrefoil.comp.back
  (:require [quatrefoil.dsl.alias :refer [create-comp group box scene text]]))

(def comp-back
  (create-comp
   :back
   {}
   (fn [mutate-view!]
     (fn [state mutate! instant]
       (box
        {:params {:width 16, :height 4, :depth 6, :x 60, :y 30},
         :material {:kind :mesh-lambert, :color 0x808080, :opacity 0.6},
         :event {:click (fn [event dispatch!] (mutate-view! :portal))}}
        (text
         {:params {:text "Back", :size 4, :height 2, :z 10},
          :material {:kind :mesh-lambert, :color 0xffcccc}}))))))
