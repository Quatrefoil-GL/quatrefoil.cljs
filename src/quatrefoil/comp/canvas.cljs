
(ns quatrefoil.comp.canvas
  (:require [quatrefoil.dsl.alias
             :refer
             [create-comp group box sphere point-light perspective-camera scene text]]
            [quatrefoil.comp.todolist :refer [comp-todolist]]
            [quatrefoil.comp.portal :refer [comp-portal]]
            [quatrefoil.comp.back :refer [comp-back]]))

(def comp-demo
  (create-comp
   :demo
   {:init-state (fn [& args] 0), :update-state (fn [state x] (inc state))}
   (fn []
     (fn [state mutate! instant]
       (group
        {}
        (box
         {:params {:width 16, :height 4, :depth 6, :x -40, :y 0, :z 0},
          :material {:kind :mesh-lambert, :color 0x808080, :opacity 0.6},
          :event {:click (fn [event dispatch!]
                    (.log js/console "Click:" event)
                    (dispatch! :demo nil)
                    (mutate! "Mutate demo"))}})
        (sphere
         {:params {:radius 8, :x 10},
          :material {:kind :mesh-lambert, :opacity 0.6, :color 0x9050c0},
          :event {:click (fn [event dispatch!]
                    (.log js/console "Click:" event)
                    (dispatch! :canvas nil))}})
        (group
         {}
         (text
          {:params {:text "Quatrefoil", :size 4, :height 2, :z 20, :x -30},
           :material {:kind :mesh-lambert, :color 0xffcccc}})))))))

(defn init-state [& args] :portal)

(defn update-state [state new-state] new-state)

(def comp-canvas
  (create-comp
   :canvas
   {:init-state init-state, :update-state update-state}
   (fn [store]
     (fn [state mutate! instant]
       (println "State:" state)
       (scene
        {}
        (case state
          :portal (comp-portal mutate!)
          :todolist (comp-todolist (:tasks store))
          :demo (comp-demo)
          nil)
        (if (not= state :portal) (comp-back mutate!))
        (point-light
         {:params {:color 0xffffff, :x 20, :y 40, :z 100, :intensity 2, :distance 400}})
        (perspective-camera
         {:params {:x 0,
                   :y 0,
                   :z 200,
                   :fov 45,
                   :aspect (/ js/window.innerWidth js/window.innerHeight),
                   :near 0.1,
                   :far 1000}}))))))
