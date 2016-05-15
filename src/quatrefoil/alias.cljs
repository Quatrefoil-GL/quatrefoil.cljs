
(ns quatrefoil.alias)

(defn create-element [element-name props children]
  {:children
   (->>
     children
     (map-indexed (fn [index child] [index child]))
     (into (sorted-map))),
   :matertial (:material props),
   :type :element,
   :event (:event props),
   :attrs (:attrs props)})

(defn create-comp [comp-name render-method]
  (fn [& args]
    {:args args,
     :name comp-name,
     :type :component,
     :render (apply render-method args)}))

(defn scene [props & children] (create-element :scene props children))

(defn light [props & children] (create-element :light props children))

(defn camera [props & children] (create-element :camara props children))

(defn group [props & children] (create-element :group props children))

(defn cube [props & children] (create-element :cube props children))
