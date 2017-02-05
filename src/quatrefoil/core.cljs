
(ns quatrefoil.core
  (:require [quatrefoil.dsl.render :refer [render-component]]
            [quatrefoil.dsl.diff :refer [diff-tree]]
            [quatrefoil.dsl.object3d-dom :refer [build-tree]]
            [quatrefoil.util.core :refer [purify-tree]]
            [quatrefoil.dsl.patch :refer [apply-changes]]))

(defonce tree-cache-ref (atom nil))

(defonce instant-variation-ref (atom []))

(defonce tree-ref (atom nil))

(defonce timestamp-ref (atom (js/Date.now)))

(defn render-canvas! [markup states-ref instants scene]
  (let [build-mutate (fn [coord new-state]
                       (println "Mutate states:" new-state)
                       (swap! states-ref assoc-in (conj coord 'data) new-state))
        queue! (fn [coord new-instant mark]
                 (swap! instant-variation-ref conj [coord new-instant mark]))
        now (js/Date.now)
        packed {:build-mutate build-mutate, :queue! queue!, :elapsed (- now @timestamp-ref)}
        new-tree (render-component
                  markup
                  @tree-cache-ref
                  []
                  (get @states-ref (:name markup))
                  (get instants (:name markup))
                  false
                  packed)]
    (reset! timestamp-ref now)
    (if (some? @tree-ref)
      (let [changes-ref (atom []), collect! (fn [x] (swap! changes-ref conj x))]
        (diff-tree @tree-ref new-tree [] collect!)
        (apply-changes @changes-ref))
      (build-tree [] (purify-tree new-tree)))
    (reset! tree-ref new-tree)
    (reset! tree-cache-ref new-tree)
    (comment .log js/console "Tree:" new-tree)
    (.log js/console "Variations" @instant-variation-ref)))

(defn clear-cache! [] (reset! tree-cache-ref nil))

(defn write-instants! [instants-ref changes]
  (doseq [change changes]
    (let [[coord new-instant mark] change]
      (swap! instants-ref assoc-in (conj coord 'data) new-instant))))
