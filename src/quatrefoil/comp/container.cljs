
(ns quatrefoil.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp div span canvas]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.text :refer [comp-text]]))

(def comp-container
  (create-comp
   :container
   (fn [store]
     (fn [state mutate!]
       (println js/window.innerWidth)
       (div
        {:style (merge ui/global)}
        (canvas
         {:attrs {:id "canvas", :width js/window.innerWidth, :height js/window.innerHeight},
          :style {}}))))))
