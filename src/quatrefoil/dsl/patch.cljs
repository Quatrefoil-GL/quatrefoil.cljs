
(ns quatrefoil.dsl.patch
  (:require [quatrefoil.dsl.object3d-dom :refer [global-scene build-tree]]
            [quatrefoil.util.core :refer [reach-object3d]]))

(defn add-children [coord op-data]
  (let [target (reach-object3d global-scene coord)]
    (doseq [entry op-data]
      (let [[k tree] entry] (.addBy target k (build-tree (conj coord k) tree))))))

(defn update-material [coord op-data]
  (println "Update material" coord op-data)
  (let [target (reach-object3d global-scene coord)]
    (.log js/console target)
    (doseq [entry op-data]
      (let [[param new-value] entry]
        (case param
          :color (set! target.material.color (js/THREE.Color. new-value))
          (do (.log js/console "Unknown param:" param)))))))

(defn remove-children [coord op-data]
  (let [target (reach-object3d global-scene coord)]
    (doseq [child-key op-data] (.removeBy target child-key))))

(defn apply-changes [changes]
  (doseq [change changes]
    (let [[coord op op-data] change]
      (.log js/console "Change:" op coord)
      (case op
        :update-material (update-material coord op-data)
        :remove-children (remove-children coord op-data)
        :add-children (add-children coord op-data)
        (do (.log js/console "Unknown op:" op))))))
