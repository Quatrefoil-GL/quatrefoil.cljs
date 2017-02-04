
(ns quatrefoil.dsl.render
  (:require [quatrefoil.util.core :refer [comp? shape? =seq? =component?]]
            [clojure.set :as set]))

(declare render-component)

(declare render-shape)

(declare render-markup)

(defn get-instant [instants init-instant args state at-place?]
  (if (contains? instants 'data)
    (get instants 'data)
    (if (fn? init-instant) (init-instant args state at-place?) nil)))

(defn updated? [markup prev-tree]
  (and (not (identical? (:args markup) (:args prev-tree)))
       (not (identical? (:states markup) (:states prev-tree)))))

(defn get-state [states init-state args]
  (if (contains? states 'data)
    (get states 'data)
    (if (fn? init-state) (apply init-state args) nil)))

(defn render-markup [markup prev-markup coord comp-coord states instants packed]
  (cond
    (and (nil? markup) (nil? prev-markup)) nil
    (and (comp? markup) (or (nil? prev-markup) (shape? prev-markup)))
      (let [k (:name markup), child-states (get states k), child-instants (get instants k)]
        (render-component markup nil coord child-states child-instants packed))
    (and (comp? prev-markup) (nil? markup))
      (let [k (:name prev-markup)
            child-states (get states k)
            child-instants (get instants k)]
        (render-component nil prev-markup coord child-states child-instants packed))
    (and (comp? prev-markup) (comp? markup) (= (:name prev-markup) (:name markup)))
      (let [k (:name markup), child-states (get states k), child-instants (get instants k)]
        (render-component markup prev-markup coord child-states child-instants packed))
    (and (comp? prev-markup) (comp? markup) (not= (:name prev-markup) (:name markup)))
      (let [k (:name markup), child-states (get states k), child-instants (get instants k)]
        (render-component markup nil coord child-states child-instants packed))
    (and (shape? markup) (or (nil? prev-markup) (comp? prev-markup)))
      (render-shape markup nil coord comp-coord states instants packed)
    (and (shape? markup) (shape? prev-markup))
      (render-shape markup prev-markup coord comp-coord states instants packed)
    (and (nil? markup) (shape? prev-markup)) nil
    :else (do (.log js/console "Unknown markup with" markup prev-markup) nil)))

(defn render-shape [markup prev-markup coord comp-coord states instants packed]
  (let [prev-children (:children prev-markup)
        children (:children markup)
        all-keys (set/union (into #{} (keys prev-children)) (into #{} (keys children)))]
    (comment .log js/console "Shape:" markup)
    (-> markup
        (assoc :coord coord)
        (assoc
         :children
         (->> all-keys
              (map
               (fn [k]
                 [k
                  (let [child (get children k), prev-child (get prev-children k)]
                    (if (and (nil? child) (nil? prev-child))
                      nil
                      (render-markup
                       child
                       prev-child
                       (conj coord k)
                       comp-coord
                       (get states k)
                       (get instants k)
                       packed)))]))
              (filter
               (fn [entry]
                 (comment .log js/console "Rendering child:" entry)
                 (some? (last entry))))
              (into {}))))))

(defn render-component [markup prev-tree coord states instants packed]
  (comment .log js/console "Component states:" states)
  (if (and (nil? markup) (nil? prev-tree))
    (do (.warn js/console "Calling render-component with nil!") nil)
    (let [base-tree (or markup prev-tree)
          comp-name (:name base-tree)
          args (:args base-tree)
          base-coord (conj coord (:name base-tree))
          render (:render base-tree)
          hooks (:hooks base-tree)
          init-instant (:init-instant hooks)
          on-tick (:on-tick hooks)
          on-unmount (:on-unmount hooks)
          on-update (:on-update hooks)
          remove? (:remove? hooks)
          state (get-state states (:init-state hooks) args)
          instant (get-instant instants init-instant args state true)
          next-instant (if (fn? on-tick)
                         (do
                          (.log js/console "Tick:" (:elapsed packed))
                          (on-tick instant (:elapsed packed)))
                         instant)
          next-instants (assoc instants 'data next-instant)
          build-mutate (:build-mutate packed)
          mutate! (fn [& state-args]
                    (let [update-state (:update-state hooks)
                          new-state (apply update-state (cons state state-args))]
                      (.log js/console "During mutate:" base-coord state new-state states)
                      (build-mutate base-coord new-state)))
          queue! (:queue! packed)
          tree (-> render
                   (apply args)
                   (apply (list state mutate! next-instant))
                   (render-markup
                    (:tree prev-tree)
                    base-coord
                    base-coord
                    states
                    instants
                    packed))
          result (merge
                  base-tree
                  {:tree tree, :states (assoc states 'data state), :instants next-instants})]
      (if (some? markup)
        (if (and (some? prev-tree) (=component? prev-tree markup))
          (do (comment .log js/console "Reusing component:" coord) prev-tree)
          (let []
            (comment .log js/console "Creating new component:" coord)
            (comment .log js/console "Comparing instants:" instant next-instant)
            (if (some? prev-tree)
              (if (and (fn? on-update) (updated? markup prev-tree))
                (let [new-instant (on-update
                                   instant
                                   (:args prev-tree)
                                   args
                                   (:states prev-tree)
                                   state)]
                  (if (not (identical? instant new-instant))
                    (queue! base-coord new-instant :update))
                  (merge result {:instants (assoc instants 'data new-instant)}))
                (do
                 (if (not= instant next-instant) (queue! base-coord next-instant :tick))
                 result))
              (do
               (if (contains? hooks :init-instant) (queue! base-coord instant :init))
               result))))
        (if (:removing? prev-tree)
          (if (fn? remove?) (if (remove? next-instant) nil result) nil)
          (if (contains? hooks :on-unmount)
            (let [new-instant (on-unmount next-instant)]
              (merge result {:instants (assoc instants 'data new-instant), :removing? true}))
            nil))))))
