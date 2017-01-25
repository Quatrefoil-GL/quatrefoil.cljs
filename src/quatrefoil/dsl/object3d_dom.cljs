
(ns quatrefoil.dsl.object3d-dom (:require [cljsjs.three]))

(defn create-element [] )

(defonce virtual-tree-ref (atom {}))

(defn remove-child [] )

(defn append-child [] )

(defn set-event [] )

(defn create-material [] )

(def camera-ref
  (atom
   (js/THREE.PerspectiveCamera. 45 (/ js/window.innerWidth js/window.innerHeight) 0.1 1000)))

(defn set-param [] )

(defn build-tree [coord tree] )

(defn set-material [] )
