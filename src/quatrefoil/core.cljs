
(ns quatrefoil.core
  (:require [cljsjs.three ]
            [quatrefoil.component.container :refer [comp-container]]
            [quatrefoil.render.expand :refer [expand-app]]
            [devtools.core :as devtools]))

(defonce store-ref (atom {}))

(defn render-page []
  (.log js/console (comp-container @store-ref))
  (let [tree (expand-app (comp-container @store-ref))]
    (.log js/console "tree:" tree)))

(defn -main []
  (enable-console-print!)
  (devtools/install!)
  (println "app started.")
  (render-page))

(set! js/window.onload -main)

(defn on-jsload [] (println "code updated.") (render-page))
