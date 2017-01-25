
(ns quatrefoil.types )

(defrecord Component [name args states instants render tree hooks removing?])

(defrecord Shape [name props children])
