
(ns quatrefoil.dsl.patch
  (:require [quatrefoil.dsl.object3d-dom :refer [global-scene build-tree]]
            [quatrefoil.util.core :refer [reach-object3d]]))

(defn add-element [coord op-data]
  (if (empty? coord)
    (.warn js/console "Cannot add element with empty coord!")
    (let [target (reach-object3d global-scene (butlast coord))]
      (.addBy target (last coord) (build-tree coord op-data)))))

(defn add-children [coord op-data]
  (let [target (reach-object3d global-scene coord)]
    (doseq [entry op-data]
      (let [[k tree] entry] (.addBy target k (build-tree (conj coord k) tree))))))

(defn remove-element [coord]
  (if (empty? coord)
    (.warn js/console "Cannot remove by empty coord!")
    (let [target (reach-object3d global-scene (butlast coord))]
      (.removeBy target (last coord)))))

(defn replace-element [coord op-data]
  (if (empty? coord)
    (.warn js/console "Cannot replace with empty coord!")
    (let [target (reach-object3d global-scene (butlast coord))]
      (.replaceBy target (last coord) (build-tree coord op-data)))))

(defn update-material [coord op-data]
  (println "Update material" coord op-data)
  (let [target (reach-object3d global-scene coord)]
    (comment .log js/console target)
    (doseq [entry op-data]
      (let [[param new-value] entry]
        (case param
          :color (.set target.material.color new-value)
          (do (.log js/console "Unknown param:" param)))))))

(defn remove-children [coord op-data]
  (let [target (reach-object3d global-scene coord)]
    (doseq [child-key op-data] (.removeBy target child-key))))

(defn update-params [coord op-data]
  (let [target (reach-object3d global-scene coord)]
    (doseq [entry op-data]
      (let [[k v] entry]
        (case k
          :x (.setX target.position v)
          :y (.setY target.position v)
          :z (.setZ target.position v)
          :radius (set! target.geometry.radius v)
          (do (.log js/console "Unknown param change:" k v)))))))

(defn apply-changes [changes]
  (doseq [change changes]
    (let [[coord op op-data] change]
      (.log js/console "Change:" op coord)
      (case op
        :add-material (update-material coord op-data)
        :update-material (update-material coord op-data)
        :remove-children (remove-children coord op-data)
        :add-children (add-children coord op-data)
        :update-params (update-params coord op-data)
        :add-params (update-params coord op-data)
        :add-element (add-element coord op-data)
        :remove-element (remove-element coord)
        :replace-element (replace-element coord op-data)
        (do (.log js/console "Unknown op:" op))))))
