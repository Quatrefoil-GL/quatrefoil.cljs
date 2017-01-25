
(ns quatrefoil.dsl.object3d-dom
  (:require [cljsjs.three] [quatrefoil.util.core :refer [purify-tree]]))

(defn create-element [element] (js/THREE.Object3D.))

(defonce virtual-tree-ref (atom {}))

(defn remove-child [] )

(defn append-child [] )

(defn set-event [] )

(defn create-material [] )

(def camera-ref
  (atom
   (js/THREE.PerspectiveCamera. 45 (/ js/window.innerWidth js/window.innerHeight) 0.1 1000)))

(defn set-param [] )

(defn build-tree [coord tree]
  (let [object3d (create-element (dissoc tree :children))
        children (->> (:children tree)
                      (map
                       (fn [entry]
                         (update entry 1 (fn [child] (build-tree (conj coord (first entry)))))))
                      (into {}))
        virtual-element {:object3d object3d, :children children}]
    (.log js/console children)
    (doseq [entry children]
      (let [child (last entry)] (.log js/console "Child:" child entry) (.add object3d child)))
    (swap! virtual-tree-ref assoc-in (conj coord 'data) virtual-element)
    object3d))

(defn set-material [] )
