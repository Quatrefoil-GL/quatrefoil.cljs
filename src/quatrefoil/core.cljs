
(ns quatrefoil.core (:require [quatrefoil.dsl.render :refer [render-component]]))

(defonce tree-ref (atom nil))

(defn render-canvas! [markup states instants]
  (let [new-tree (render-component markup @tree-ref [] states instants)]
    (reset! tree-ref new-tree)
    (.log js/console "Tree:" new-tree)))
