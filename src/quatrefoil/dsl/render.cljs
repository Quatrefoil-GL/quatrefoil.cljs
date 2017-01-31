
(ns quatrefoil.dsl.render (:require [quatrefoil.util.core :refer [comp? shape? =seq?]]))

(declare render-component)

(declare render-shape)

(declare render-markup)

(defn render-markup [markup prev-markup coord comp-coord states build-mutate instants]
  (if (comp? markup)
    (render-component
     markup
     prev-markup
     coord
     (get states (:name markup))
     build-mutate
     instants)
    (render-shape markup prev-markup coord comp-coord states build-mutate instants)))

(defn render-shape [markup prev-markup coord comp-coord states build-mutate instants]
  (let [prev-children (:children prev-markup)]
    (comment .log js/console "Shape:" markup)
    (-> markup
        (assoc :coord coord)
        (update
         :children
         (fn [children]
           (->> children
                (map
                 (fn [entry]
                   (update
                    entry
                    1
                    (fn [child]
                      (let [k (first entry)]
                        (render-markup
                         child
                         (get prev-children k)
                         (conj coord k)
                         comp-coord
                         (get states k)
                         build-mutate
                         (get instants k)))))))
                (into {})))))))

(defn render-component [markup prev-tree coord states build-mutate instants]
  (comment .log js/console "Component states:" states)
  (if (and (some? prev-tree)
           (let [prev-args (:args prev-tree)
                 prev-states (:states prev-tree)
                 prev-instants (:instants prev-tree)]
             (and (=seq? (:args markup) prev-args)
                  (identical? states prev-states)
                  (identical? instants prev-instants))))
    prev-tree
    (let [comp-name (:name markup)
          base-coord (conj coord comp-name)
          hooks (:hooks markup)
          state (if (contains? states 'data)
                  (get states 'data)
                  (let [init-state (:init-state hooks)] (apply init-state (:args markup))))
          update-state (:update-state hooks)
          mutate! (fn [& args]
                    (let [new-state (apply update-state (cons state args))]
                      (.log js/console "During mutate:" base-coord state new-state states)
                      (build-mutate base-coord new-state)))
          instant (get instants comp-name)
          tree (-> (:render markup)
                   (apply (:args markup))
                   (apply (list state mutate! (get instants 'data)))
                   (render-markup
                    (:tree prev-tree)
                    base-coord
                    base-coord
                    states
                    build-mutate
                    instant))]
      (merge markup {:tree tree, :states states, :instants instants}))))
