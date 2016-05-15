
(ns quatrefoil.render.geometry)

(defn render-geometry-dsl [kind args]
  {:pre [(vector? args)]}
  (case
    kind
    :box
    (let [[x y z] args] (THREE.BoxGeometry. x y z))
    :sphere
    (let [[radius w-segments h-segments] args]
      (THREE.SphereGeometry. radius w-segments h-segments))
    (throw (str "Geometry not found:" kind))))
