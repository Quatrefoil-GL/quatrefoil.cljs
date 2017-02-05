
(ns quatrefoil.dsl.render
  (:require [quatrefoil.util.core :refer [comp? shape? =seq? =component?]]
            [clojure.set :as set]))

(declare render-component)

(declare render-shape)

(declare render-markup)

(defn get-instant [instants init-instant args state at-place? has-prev?]
  (if (and has-prev? (contains? instants 'data))
    (get instants 'data)
    (if (fn? init-instant) (init-instant args state at-place?) nil)))

(defn updated? [markup prev-tree]
  (and (not (identical? (:args markup) (:args prev-tree)))
       (not (identical? (:states markup) (:states prev-tree)))))

(defn defaut-tick [instant elapsed] instant)

(defn get-state [states init-state args]
  (if (contains? states 'data)
    (get states 'data)
    (if (fn? init-state) (apply init-state args) nil)))

(defn render-markup [markup prev-markup coord comp-coord states instants new? packed]
  (cond
    (and (nil? markup) (nil? prev-markup)) nil
    (and (comp? markup) (or (nil? prev-markup) (shape? prev-markup)))
      (let [k (:name markup), child-states (get states k), child-instants (get instants k)]
        (render-component markup nil coord child-states child-instants new? packed))
    (and (comp? prev-markup) (nil? markup))
      (let [k (:name prev-markup)
            child-states (get states k)
            child-instants (get instants k)]
        (render-component nil prev-markup coord child-states child-instants new? packed))
    (and (comp? prev-markup) (comp? markup) (= (:name prev-markup) (:name markup)))
      (let [k (:name markup), child-states (get states k), child-instants (get instants k)]
        (render-component markup prev-markup coord child-states child-instants new? packed))
    (and (comp? prev-markup) (comp? markup) (not= (:name prev-markup) (:name markup)))
      (let [k (:name markup), child-states (get states k), child-instants (get instants k)]
        (render-component markup nil coord child-states child-instants new? packed))
    (and (shape? markup) (or (nil? prev-markup) (comp? prev-markup)))
      (render-shape markup nil coord comp-coord states instants new? packed)
    (and (shape? markup) (shape? prev-markup))
      (render-shape markup prev-markup coord comp-coord states instants new? packed)
    (and (nil? markup) (shape? prev-markup)) nil
    :else (do (.log js/console "Unknown markup with" markup prev-markup) nil)))

(defn render-shape [markup prev-markup coord comp-coord states instants new? packed]
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
                       new?
                       packed)))]))
              (filter
               (fn [entry]
                 (comment .log js/console "Rendering child:" entry)
                 (some? (last entry))))
              (into {}))))))

(defn render-component [markup prev-markup coord states instants new? packed]
  (comment .log js/console "Component states:" states)
  (comment println "Instants:" coord instants)
  (if (and (nil? markup) (nil? prev-markup))
    (do (.warn js/console "Calling render-component with nil!") nil)
    (let [elapsed (:elapsed packed)
          base-tree (or markup prev-markup)
          comp-name (:name base-tree)
          args (:args base-tree)
          base-coord (conj coord (:name base-tree))
          render (:render base-tree)
          hooks (:hooks base-tree)
          init-instant (:init-instant hooks)
          on-tick (or (:on-tick hooks) defaut-tick)
          on-unmount (:on-unmount hooks)
          on-update (:on-update hooks)
          remove? (:remove? hooks)
          state (get-state states (:init-state hooks) args)
          at-place? (and (not new?) (nil? prev-markup))
          instant (get-instant
                   instants
                   init-instant
                   args
                   state
                   at-place?
                   (some? prev-markup))
          build-mutate (:build-mutate packed)
          mutate! (fn [& state-args]
                    (let [update-state (:update-state hooks)
                          new-state (apply update-state (cons state state-args))]
                      (.log js/console "During mutate:" base-coord state new-state states)
                      (build-mutate base-coord new-state)))
          queue! (:queue! packed)
          curry-render (apply render args)
          cached-tree (if (some? prev-markup) (:tree prev-markup) nil)
          render-result (fn [the-instant removing?]
                          (let [tree-markup (curry-render state mutate! the-instant)
                                tree (render-markup
                                      tree-markup
                                      cached-tree
                                      base-coord
                                      base-coord
                                      states
                                      instants
                                      true
                                      packed)]
                            (merge
                             base-tree
                             {:tree tree,
                              :states (assoc states 'data state),
                              :instants (assoc instants :data the-instant),
                              :removing? removing?})))]
      (cond
        (and (some? markup) (nil? prev-markup))
          (do
           (if (some? instant) (queue! base-coord instant :init))
           (render-result instant false))
        (and (some? markup) (some? prev-markup) (=component? prev-markup markup))
          (do (comment .log js/console "Reusing component:" coord) prev-markup)
        (and (some? markup) (some? prev-markup))
          (if (and (fn? on-update) (updated? markup prev-markup))
            (let [new-instant (on-update
                               instant
                               (:args prev-markup)
                               args
                               (:states prev-markup)
                               state)]
              (if (not= instant new-instant)
                (do (queue! base-coord new-instant :update) (render-result new-instant false))
                (let [ticked-instant (on-tick instant elapsed)]
                  (if (not= instant ticked-instant)
                    (queue! base-coord ticked-instant :tick-on-update))
                  (render-result ticked-instant false))))
            (let [new-instant (on-tick instant elapsed)]
              (if (not= instant new-instant) (queue! base-coord new-instant :tick))
              (render-result new-instant false)))
        (and (nil? markup) (some? prev-markup) (:removing? prev-markup))
          (let [new-instant (on-tick instant elapsed)]
            (if (remove? new-instant)
              nil
              (do
               (queue! base-coord new-instant :removing)
               (render-result new-instant true))))
        (and (nil? markup) (some? prev-markup) (not (:removing? prev-markup)))
          (if (fn? on-unmount)
            (let [new-instant (on-unmount instant)]
              (queue! base-coord new-instant :unmount)
              (render-result new-instant true))
            nil)
        :else (do (.warn js/console "Unexpected case:" markup prev-markup))))))
