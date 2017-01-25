
(ns quatrefoil.dsl.render (:require [quatrefoil.util.core :refer [comp? shape? =seq?]]))

(declare render-component)

(declare render-shape)

(declare render-markup)

(defn render-markup [markup prev-markup coord comp-coord states instants]
  (if (comp? markup)
    (render-component markup prev-markup coord states instants)
    (render-shape markup prev-markup coord coord states instants)))

(defn render-shape [markup prev-markup coord comp-coord states instants]
  (let [prev-children (:children prev-markup)]
    (update
     markup
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
                     (get instants k)))))))
            (into {}))))))

(defn render-component [markup prev-tree coord states instants]
  (if (and (some? prev-tree)
           (let [prev-args (:args prev-tree)
                 prev-states (:states prev-tree)
                 prev-instants (:instants prev-tree)]
             (and (=seq? (:args markup) prev-args)
                  (identical? states prev-states)
                  (identical? instants prev-instants))))
    prev-tree
    (let [comp-name (:name markup)
          mutate! (fn [& args] (println "Mutate:" args))
          tree (-> (:render markup)
                   (apply (:args markup))
                   (apply (list (get states 'data) mutate! (get instants 'data)))
                   (render-markup
                    (:tree prev-tree)
                    (conj coord comp-name)
                    coord
                    (get states comp-name)
                    (get instants comp-name)))]
      (merge markup {:tree tree, :states states, :instants instants}))))
