
(ns quatrefoil.main
  (:require [respo.core
             :refer
             [render! clear-cache! falsify-stage! render-element gc-states!]]
            [quatrefoil.comp.container :refer [comp-container]]
            [cljs.reader :refer [read-string]]
            [quatrefoil.core :refer [render-canvas!]]
            [quatrefoil.comp.canvas :refer [comp-canvas]]
            [devtools.core :as devtools]))

(defn dispatch! [op op-data] )

(defonce instants-ref (atom {}))

(defonce store-ref (atom {}))

(defonce states-ref (atom {}))

(defn render-canvas-app! []
  (render-canvas! (comp-canvas @store-ref) @states-ref @instants-ref))

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
  (render-canvas-app!)
  (add-watch store-ref :changes render-canvas-app!)
  (add-watch states-ref :changes render-canvas-app!)
  (println "App started!"))

(defn on-jsload! [] (render-canvas-app!) (println "Code updated."))

(set! (.-onload js/window) -main!)
