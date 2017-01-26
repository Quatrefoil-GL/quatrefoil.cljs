
(ns quatrefoil.dsl.object3d-dom
  (:require [cljsjs.three] [quatrefoil.util.core :refer [purify-tree]]))

(defn create-material [material]
  (case (:kind material)
    :line-basic (js/THREE.LineBasicMaterial. (clj->js (dissoc material :kind)))
    :mesh-basic (js/THREE.MeshBasicMaterial. (clj->js (dissoc material :kind)))
    (do
     (.warn js/console "Unknown material:" material)
     (js/THREE.LineBasicMaterial. (clj->js (dissoc material :kind))))))

(defn create-sphere-element [params material event]
  (let [geometry (js/THREE.SphereGeometry.
                  (or (:radius params) 8)
                  (or (:width-segments params) 32)
                  (or (:height-segments params) 32))
        object3d (js/THREE.Mesh. geometry (create-material material))]
    object3d))

(defn create-box-element [params material event]
  (let [geometry (js/THREE.BoxGeometry. (:width params) (:height params) (:depth params))
        object3d (js/THREE.Mesh. geometry (create-material material))]
    object3d))

(defn create-element [element]
  (.log js/console "Element:" element)
  (let [params (:params element), material (:material element), event (:event element)]
    (case (:name element)
      :group (js/THREE.Group.)
      :box (create-box-element params material event)
      :sphere (create-sphere-element params material event)
      (do (.warn js/console "Unknown element" element) (js/THREE.Object3D.)))))

(defonce virtual-tree-ref (atom {}))

(defn remove-child [] )

(defn append-child [] )

(defn set-event [] )

(defonce camera-ref
  (atom
   (let [ratio (/ js/window.innerWidth js/window.innerHeight)
         camera (js/THREE.PerspectiveCamera. 45 ratio 0.1 1000)]
     (set! camera.position.x 10)
     (set! camera.position.y 10)
     (set! camera.position.z 80)
     camera)))

(defn set-param [] )

(defn build-tree [coord tree]
  (let [object3d (create-element (dissoc tree :children))
        children (->> (:children tree)
                      (map
                       (fn [entry]
                         (update
                          entry
                          1
                          (fn [child] (build-tree (conj coord (first entry)) child)))))
                      (into {}))
        virtual-element {:object3d object3d, :children children}]
    (doseq [entry children]
      (let [child (last entry)] (.log js/console "Child:" child entry) (.add object3d child)))
    (swap! virtual-tree-ref assoc-in (conj coord 'data) virtual-element)
    object3d))

(defn set-material [] )
