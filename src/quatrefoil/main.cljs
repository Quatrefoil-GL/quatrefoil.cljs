
(ns quatrefoil.main (:require [cljs.reader :refer [read-string]]))

(defn dispatch! [op op-data] )

(defn -main! [] (enable-console-print!) (println "App started!"))

(defonce store-ref (atom {}))

(defonce states-ref (atom {}))

(defn on-jsload! [] (println "Code updated."))

(set! (.-onload js/window) -main!)
