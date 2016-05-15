
(ns quatrefoil.render.expand)

(declare expand-markup)

(declare expand-element)

(def default-state {})

(def default-instant {})

(def default-tick 0)

(defn default-mutate [])

(defn expand-element [markup coord]
  (-> markup
   (update
     :children
     (fn [children]
       (->>
         children
         (map
           (fn [entry]
             (let [child-key (key entry) child (val entry)]
               [child-key
                (expand-markup child (conj coord child-key))])))
         (into (sorted-map)))))))

(defn expand-comp [markup coord]
  (let [r1 (:render markup)
        r2 (r1 default-state default-mutate)
        r3 (r2 default-instant default-tick)
        tree (expand-element r3 coord)]
    tree))

(defn expand-markup [markup coord]
  (if (= :component (:type markup))
    (expand-comp markup coord)
    (expand-element markup coord)))

(defn expand-app [markup] (expand-comp markup []))
