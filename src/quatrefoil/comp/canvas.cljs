
(ns quatrefoil.comp.canvas
  (:require [quatrefoil.dsl.alias
             :refer
             [create-comp group box sphere point-light perspective-camera scene text]]))

(def comp-demo
  (create-comp
   :demo
   {:init-state (fn [& args] 0), :update-state (fn [state x] (inc state))}
   (fn []
     (fn [state mutate! instant]
       (group
        {}
        (box
         {:params {:width 16, :height 4, :depth 6},
          :material {:kind :mesh-lambert, :color 0x808080, :opacity 0.6},
          :event {:click (fn [event dispatch!]
                    (.log js/console "Click:" event)
                    (dispatch! :demo nil)
                    (mutate! "Mutate demo"))}})
        (group
         {}
         (text
          {:params {:text "Quatrefoil", :size 4, :height 2, :z 40},
           :material {:kind :mesh-lambert, :color 0xffcccc}})))))))

(def comp-canvas
  (create-comp
   :canvas
   {}
   (fn [store]
     (fn [state mutate! instant]
       (scene
        {}
        (comp-demo)
        (sphere
         {:params {:radius 4, :x 40},
          :material {:kind :mesh-lambert, :opacity 0.6, :color 0x9050c0},
          :event {:click (fn [event dispatch!]
                    (.log js/console "Click:" event)
                    (dispatch! :canvas nil))}})
        (point-light
         {:params {:color 0xffaaaa, :x 0, :y 40, :z 60, :intensity 2, :distance 400}})
        (perspective-camera
         {:params {:x 0,
                   :y 0,
                   :z 200,
                   :fov 45,
                   :aspect (/ js/window.innerWidth js/window.innerHeight),
                   :near 0.1,
                   :far 1000}}))))))
