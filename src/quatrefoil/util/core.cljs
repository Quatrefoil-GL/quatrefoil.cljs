
(ns quatrefoil.util.core (:require [quatrefoil.types :refer [Component Shape]]))

(defn =seq? [xs ys]
  (let [xs-empty? (empty? xs), ys-empty? (empty? ys)]
    (if xs-empty?
      ys-empty?
      (if ys-empty?
        (if (identical? (first xs) (first ys)) (recur (rest xs) (rest ys)) false)))))

(defn comp? [x] (= Component (type x)))

(defn shape? [x] (= Shape (type x)))
