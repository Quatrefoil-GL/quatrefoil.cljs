
(ns quatrefoil.comp.fade-in-out
  (:require [quatrefoil.dsl.alias :refer [create-comp group box sphere text]]))

(defn init-instant [args state at-place?] {:presence 0, :presence-v 1})

(defn on-tick [instant elapsed]
  (let [next-presence (+ (:presence instant) (* elapsed (:presence-v instant)))]
    (.log js/console "Next presence:" next-presence elapsed (:presence-v instant))
    (if (<= next-presence 0)
      {:presence 0, :presence-v 0}
      (if (>= next-presence 1000)
        {:presence 1000, :presence-v 0}
        (assoc instant :presence next-presence)))))

(defn on-unmount [instant] {:presence 1000, :presence-v -1})

(defn on-update [instant old-args args old-state state] instant)

(defn remove? [instant] (<= (:presence instant) 0))

(def comp-fade-in-out
  (create-comp
   :fade-in-out
   {:init-instant init-instant,
    :on-update on-update,
    :on-unmount on-unmount,
    :on-tick on-tick,
    :remove? remove?}
   (fn [inside]
     (fn [state mutate! instant]
       (println "Fading:" instant)
       (let [ratio (/ (:presence instant) 1000)]
         (group {:params {:scale-x ratio, :scale-y ratio, :scale-z ratio}} inside))))))
