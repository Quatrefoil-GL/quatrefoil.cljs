
(ns quatrefoil.core
  (:require [quatrefoil.dsl.render :refer [render-component]]
            [quatrefoil.dsl.diff :refer [diff-tree]]
            [quatrefoil.dsl.object3d-dom :refer [build-tree]]
            [quatrefoil.util.core :refer [purify-tree]]
            [quatrefoil.dsl.patch :refer [apply-changes]]))

(defonce tree-cache-ref (atom nil))

(defonce tree-ref (atom nil))

(defn render-canvas! [markup states-ref instants scene]
  (let [build-mutate (fn [coord new-state]
                       (println "Mutate states:" new-state)
                       (swap! states-ref assoc-in (conj coord 'data) new-state))
        new-tree (render-component
                  markup
                  @tree-cache-ref
                  []
                  (get @states-ref (:name markup))
                  build-mutate
                  instants)]
    (if (some? @tree-ref)
      (let [changes-ref (atom []), collect! (fn [x] (swap! changes-ref conj x))]
        (diff-tree @tree-ref new-tree [] collect!)
        (.log js/console "Changes:" @changes-ref)
        (apply-changes @changes-ref))
      (build-tree [] (purify-tree new-tree)))
    (reset! tree-ref new-tree)
    (reset! tree-cache-ref new-tree)
    (comment .log js/console "Tree:" new-tree)))

(defn clear-cache! [] (reset! tree-cache-ref nil))
