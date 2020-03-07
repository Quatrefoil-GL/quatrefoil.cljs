
(ns quatrefoil.main
  (:require ["./alter-object3d" :as altered]
            [cljs.reader :refer [read-string]]
            [quatrefoil.core
             :refer
             [render-canvas! tree-ref clear-cache! instant-variation-ref write-instants!]]
            [quatrefoil.comp.canvas :refer [comp-canvas]]
            [quatrefoil.dsl.object3d-dom
             :refer
             [camera-ref global-scene on-canvas-click ref-dirty-call!]]
            [quatrefoil.updater.core :refer [updater]]
            ["three" :as THREE]))

(defonce store-ref (atom {:tasks {100 {:id 100, :text "Initial task", :done? false}}}))

(defn dispatch! [op op-data]
  (let [store (updater @store-ref op op-data)]
    (.log js/console "Dispatch:" op op-data store)
    (reset! store-ref store)))

(defonce instants-ref (atom {}))

(defonce ref-task (atom nil))

(defonce renderer-ref (atom nil))

(defonce states-ref (atom {}))

(defn render-canvas-app! []
  (if (some? @ref-task) (do (js/clearTimeout @ref-task) (reset! ref-task nil)))
  (comment println "Render app:" (pr-str @instants-ref))
  (render-canvas! (comp-canvas @store-ref) states-ref @instants-ref global-scene)
  (.render @renderer-ref global-scene @camera-ref)
  (if (not (empty? @instant-variation-ref))
    (do
     (write-instants! instants-ref @instant-variation-ref)
     (reset! instant-variation-ref [])
     (reset!
      ref-task
      (js/requestAnimationFrame (fn [] (reset! ref-task nil) (render-canvas-app!)) 40)))))

(defn app-main! []
  (let [canvas-el (js/document.querySelector "canvas")]
    (reset!
     renderer-ref
     (THREE/WebGLRenderer. (clj->js {:canvas canvas-el, :antialias true})))
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

(defn main! [] (reset! ref-dirty-call! (fn [] (js/setTimeout app-main! 100))))

(defn reload! [] (clear-cache!) (render-canvas-app!) (println "Code updated."))
