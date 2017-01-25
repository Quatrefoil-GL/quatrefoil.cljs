
(ns quatrefoil.core )

(defn render-canvas! [markup] (.log js/console "Markup:" markup))

(defonce markup-ref (atom nil))
