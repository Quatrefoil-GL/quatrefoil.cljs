
(ns quatrefoil.render.core
  (:require [quatrefoil.render.material :refer [render-material-dsl]]
            [quatrefoil.render.geometry :refer [render-geometry-dsl]]))

(declare render-markup)

(defn render-element [markup]
  (let [el (case
             (:name markup)
             :scene
             (THREE.Scene.)
             :light
             (let [[color intensity distance] (:args markup)
                   attrs (:attrs markup)
                   position (or (:position attrs) [10 10 10])
                   [x y z] position
                   light (THREE.PointLight. color intensity distance)]
               (println "light position:" position color)
               (.set light.position x y y)
               light)
             :group
             (THREE.Object3D.)
             (let [geometry (render-geometry-dsl
                              (:name markup)
                              (:args markup))
                   material (render-material-dsl (:material markup))
                   mesh (THREE.Mesh. geometry material)]
               mesh))]
    (doseq [child-entry (:children markup)]
      (.add el (render-markup (val child-entry))))
    (.log js/console "result of el:" el)
    el))

(defn render-component [markup] (render-element (:tree markup)))

(defn render-markup [markup]
  (if (= :component (:type markup))
    (render-component markup)
    (render-element markup)))
