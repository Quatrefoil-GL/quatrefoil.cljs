
(ns quatrefoil.dsl.object3d-dom (:require [cljsjs.three]))

(def camera-ref
  (atom
   (js/THREE.PerspectiveCamera. 45 (/ js/window.innerWidth js/window.innerHeight) 0.1 1000)))

(defn set-event [] )

(defn remove-child [] )

(defn create-element [] )

(defn set-material [] )

(defn create-material [] )

(defn append-child [] )

(defn set-param [] )
