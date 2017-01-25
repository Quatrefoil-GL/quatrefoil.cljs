
(ns quatrefoil.dsl.diff (:require [clojure.set :as set]))

(defn diff-params [prev-params params coord collect!] )

(defn diff-events [prev-events events coord collect!]
  (let [prev-event-names (into #{} (keys prev-events))
        event-names (into #{} (keys events))
        added-events (set/difference event-names prev-event-names)
        removed-events (set/difference prev-event-names event-names)]
    (if (not (empty? added-events)) (collect! [coord :add-events added-events]))
    (if (not (empty? removed-events)) (collect! [coord :remove-events removed-events]))))

(defn diff-material [prev-material material coord collect!] nil)

(defn diff-tree [prev-tree tree coord collect!]
  (if (some? prev-tree)
    (if (some? tree)
      (do
       (diff-params (:params prev-tree) (:params tree) coord collect!)
       (diff-material (:material prev-tree) (:material tree) coord collect!)
       (diff-events (:event prev-tree) (:event tree) coord collect!))
      (collect! [coord :remove]))
    (if (some? tree) (collect! [:create tree]) nil)))
