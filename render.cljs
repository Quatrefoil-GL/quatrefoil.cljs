
(ns ssr-stages.boot
  (:require
    [respo.alias :refer [html head title script style meta' div link body canvas]]
    [respo.render.html :refer [make-html]]
    [quatrefoil.comp.container :refer [comp-container]]))

(defn html-dsl [data ssr-stages]
  (make-html
    (html {}
      (head {}
        (title {:attrs {:innerHTML "Quatrefoil"}})
        (link {:attrs {:rel "icon" :type "image/png"
                       :href "https://avatars1.githubusercontent.com/u/10102840?v=3&s=200"}})
        (link {:attrs {:rel "stylesheet" :type "text/css" :href "style.css"}})
        (link (:attrs {:rel "manifest" :href "manifest.json"}))
        (meta' {:attrs {:charset "utf-8"}})
        (meta' {:attrs {:name "viewport" :content "width=device-width, initial-scale=1"}})
        (meta' {:attrs {:id "ssr-stages" :content (pr-str ssr-stages)}})
        (style {:attrs {:innerHTML "body {margin: 0;}"}})
        (style {:attrs {:innerHTML "body * {box-sizing: border-box;}"}})
        (script {:attrs {:id "config" :type "text/edn" :innerHTML (pr-str data)}}))
      (body {}
        (canvas {:attrs {:id "app"}})
        (script {:attrs {:src "main.js"}})))))

(defn generate-html [ssr-stages]
  (html-dsl {:build? true} ssr-stages))

(defn spit [file-name content]
  (let [fs (js/require "fs")]
    (.writeFileSync fs file-name content)))

(defn -main []
  (spit "target/index.html" (generate-html #{:shell})))

(-main)
