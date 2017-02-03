
(ns quatrefoil.comp.portal
  (:require [quatrefoil.dsl.alias :refer [create-comp group box sphere text]]))

(def comp-portal
  (create-comp
   :portal
   {}
   (fn [mutate-view!]
     (fn [state mutate! instant]
       (group
        {}
        (box
         {:params {:width 16, :height 4, :depth 6, :x -40, :y 30, :z 0},
          :material {:kind :mesh-lambert, :color 0xccc80, :opacity 0.6},
          :event {:click (fn [event dispatch!]
                    (.log js/console "Click:" event)
                    (mutate-view! :todolist))}}
         (text
          {:params {:text "Todolist", :size 4, :height 2, :z 40, :x 0},
           :material {:kind :mesh-lambert, :color 0xffcccc}}))
        (box
         {:params {:width 16, :height 4, :depth 6, :x 0, :y 30},
          :material {:kind :mesh-lambert, :color 0xccc80, :opacity 0.6},
          :event {:click (fn [event dispatch!]
                    (.log js/console "Click:" event)
                    (mutate-view! :demo))}}
         (text
          {:params {:text "Demo", :size 4, :height 2, :z 40, :x 0},
           :material {:kind :mesh-lambert, :color 0xffcccc}})))))))
