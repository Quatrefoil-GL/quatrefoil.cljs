
(ns quatrefoil.main
  (:require [respo.core
             :refer
             [render! clear-cache! falsify-stage! render-element gc-states!]]
            [quatrefoil.comp.container :refer [comp-container]]
            [cljs.reader :refer [read-string]]
            [quatrefoil.core :refer [render-canvas!]]
            [quatrefoil.comp.canvas :refer [comp-canvas]]
            [devtools.core :as devtools]
            [cljsjs.three]
            [quatrefoil.dsl.object3d-dom :refer [camera-ref]]))

(defn dispatch! [op op-data] )

(defonce instants-ref (atom {}))

(defonce renderer-ref (atom nil))

(defonce store-ref (atom {}))

(defonce states-ref (atom {}))

(defonce scene (js/THREE.Scene.))

(defn render-canvas-app! []
  (render-canvas! (comp-canvas @store-ref) @states-ref @instants-ref scene)
  (.render @renderer-ref scene @camera-ref))

(defn render-app! []
  (let [target (.querySelector js/document "#app")]
    (render! (comp-container @store-ref) target dispatch! states-ref)))

(def ssr-stages
  (let [ssr-element (.querySelector js/document "#ssr-stages")
        ssr-markup (.getAttribute ssr-element "content")]
    (read-string ssr-markup)))

(defn -main! []
  (enable-console-print!)
  (devtools/install!)
  (if (not (empty? ssr-stages))
    (let [target (.querySelector js/document "#app")]
      (falsify-stage!
       target
       (render-element (comp-container @store-ref ssr-stages) states-ref)
       dispatch!)))
  (render-app!)
  (reset!
   renderer-ref
   (js/THREE.WebGLRenderer.
    (clj->js {:canvas (js/document.querySelector "canvas"), :antialias true})))
  (.setSize @renderer-ref js/window.innerWidth js/window.innerHeight)
  (render-canvas-app!)
  (add-watch store-ref :changes render-canvas-app!)
  (add-watch states-ref :changes render-canvas-app!)
  (println "App started!"))

(defn on-jsload! [] (render-canvas-app!) (println "Code updated."))

(set! (.-onload js/window) -main!)
