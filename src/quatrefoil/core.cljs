
(ns quatrefoil.core
  (:require [quatrefoil.dsl.render :refer [render-component]]
            [quatrefoil.dsl.diff :refer [diff-tree]]
            [quatrefoil.dsl.object3d-dom :refer [build-tree]]
            [quatrefoil.util.core :refer [purify-tree]]))

(defonce tree-ref (atom nil))

(defn render-canvas! [markup states instants scene]
  (let [new-tree (render-component markup @tree-ref [] states instants)]
    (if (some? @tree-ref)
      (let [changes-ref (atom []), collect! (fn [x] (swap! changes-ref conj x))]
        (diff-tree @tree-ref new-tree [] collect!)
        (.log js/console @changes-ref))
      (.add scene (build-tree [] (purify-tree new-tree))))
    (reset! tree-ref new-tree)
    (.log js/console "Tree:" new-tree)))
