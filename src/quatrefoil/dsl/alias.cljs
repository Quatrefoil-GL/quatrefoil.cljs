
(ns quatrefoil.dsl.alias
  (:require [quatrefoil.types :refer [Shape Component]]
            [quatrefoil.util.core :refer [comp? shape?]]))

(defn arrange-children [children]
  (let [cursor (first children)
        result (if (and (= 1 (count children)) (not (or (comp? cursor) (shape? cursor))))
                 (->> cursor (filter (fn [entry] (some? (last entry)))) (into {}))
                 (->> children
                      (map-indexed vector)
                      (filter (fn [entry] (some? (last entry))))
                      (into {})))]
    (comment .log js/console "Handle children:" children result)
    result))

(defn create-element [el-name props children]
  (Shape.
   el-name
   (:params props)
   (:material props)
   (:event props)
   (arrange-children children)
   nil))

(defn point-light [props & children] (create-element :point-light props children))

(defn perspective-camera [props & children]
  (create-element :perspective-camera props children))

(defn group [props & children] (create-element :group props children))

(defn camera [props & children] (create-element :camera props children))

(defn create-comp [comp-name hooks render]
  (fn [& args] (Component. comp-name args {} {} render nil hooks false)))

(defn box [props & children] (create-element :box props children))

(defn text [props & children] (create-element :text props children))

(defn line [props & children] (create-element :line props children))

(defn sphere [props & children] (create-element :sphere props children))

(defn scene [props & children] (create-element :scene props children))
