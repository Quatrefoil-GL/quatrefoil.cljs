
(ns quatrefoil.main
  (:require [respo.core
             :refer
             [render! clear-cache! falsify-stage! render-element gc-states!]]
            [quatrefoil.comp.container :refer [comp-container]]
            [cljs.reader :refer [read-string]]
            [quatrefoil.core
             :refer
             [render-canvas! tree-ref clear-cache! instant-variation-ref write-instants!]]
            [quatrefoil.comp.canvas :refer [comp-canvas]]
            [devtools.core :as devtools]
            [quatrefoil.dsl.object3d-dom :refer [camera-ref global-scene on-canvas-click]]
            [quatrefoil.updater.core :refer [updater]]))

(defonce store-ref (atom {:tasks {100 {:id 100, :text "Initial task", :done? false}}}))

(defn dispatch! [op op-data]
  (let [store (updater @store-ref op op-data)]
    (.log js/console "Dispatch:" op op-data store)
    (reset! store-ref store)))

(defonce instants-ref (atom {}))

(defonce renderer-ref (atom nil))

(defonce states-ref (atom {}))

(defn render-canvas-app! []
  (.log js/console "Rerender" @store-ref @states-ref @instants-ref)
  (reset! instant-variation-ref [])
  (render-canvas! (comp-canvas @store-ref) states-ref @instants-ref global-scene)
  (.render @renderer-ref global-scene @camera-ref)
  (if (not (empty? @instant-variation-ref))
    (do
     (write-instants! instants-ref @instant-variation-ref)
     (js/setTimeout render-canvas-app! 100))))

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
  (let [canvas-el (js/document.querySelector "canvas")]
    (reset!
     renderer-ref
     (js/THREE.WebGLRenderer. (clj->js {:canvas canvas-el, :antialias true})))
    (.setPixelRatio @renderer-ref (or js/window.devicePixelRatio 1))
    (.addEventListener
     canvas-el
     "click"
     (fn [event] (on-canvas-click event dispatch! tree-ref))))
  (.setSize @renderer-ref js/window.innerWidth js/window.innerHeight)
  (render-canvas-app!)
  (add-watch store-ref :changes render-canvas-app!)
  (add-watch states-ref :changes render-canvas-app!)
  (println "App started!"))

(defn on-jsload! [] (clear-cache!) (render-canvas-app!) (println "Code updated."))

(set! (.-onload js/window) -main!)
