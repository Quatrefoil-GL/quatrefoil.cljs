
(ns quatrefoil.core
  (:require [cljsjs.three ]
            [quatrefoil.component.container :refer [comp-container]]
            [quatrefoil.render.expand :refer [expand-app]]
            [devtools.core :as devtools]
            [quatrefoil.render.core :refer [render-markup]]))

(defonce store-ref (atom {}))

(defn render-page []
  (.log js/console (comp-container @store-ref))
  (let [tree (expand-app (comp-container @store-ref))
        target (.querySelector js/document "#app")
        renderer (THREE.WebGLRenderer. (js-obj "canvas" target))
        w 600
        h 800
        scene (render-markup tree)
        camera (THREE.PerspectiveCamera. 40 (/ w h) 0.1 1000)]
    (.log js/console "tree:" tree)
    (.log js/console "render scene:" scene)
    (.set camera.position 0 0 30)
    (.lookAt camera scene.position)
    (.setSize renderer w h)
    (.setClearColor renderer 0xdddddd 1)
    (.render renderer scene camera)))

(defn -main []
  (enable-console-print!)
  (devtools/install!)
  (println "app started.")
  (render-page))

(set! js/window.onload -main)

(defn on-jsload [] (println "code updated.") (render-page))
