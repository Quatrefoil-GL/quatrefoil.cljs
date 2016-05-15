
(ns quatrefoil.alias)

(defn create-element [element-name props children]
  {:args (:args props),
   :children
   (->>
     children
     (map-indexed (fn [index child] [index child]))
     (into (sorted-map))),
   :name element-name,
   :type :element,
   :event (:event props),
   :material (:material props),
   :attrs (:attrs props)})

(defn create-comp [comp-name render-method]
  (fn [& args]
    {:args args,
     :name comp-name,
     :type :component,
     :render (apply render-method args)}))

(defn scene [props & children] (create-element :scene props children))

(defn light [props & children] (create-element :light props children))

(defn group [props & children] (create-element :group props children))

(defn box [props & children] (create-element :box props children))

(defn sphere [props & children] (create-element :sphere props children))
