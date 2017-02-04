
(ns ssr-stages.boot
  (:require
    [respo.alias :refer [html head title script style meta' div link body]]
    [respo.render.html :refer [make-html make-string]]
    [quatrefoil.comp.container :refer [comp-container]]))

(defn html-dsl [data content ssr-stages]
  (make-html
    (html {}
      (head {}
        (title {:attrs {:innerHTML "Quatrefoil"}})
        (link {:attrs {:rel "icon" :type "image/png"
                       :href "https://avatars1.githubusercontent.com/u/10102840?v=3&s=200"}})
        (link (:attrs {:rel "manifest" :href "manifest.json"}))
        (meta' {:attrs {:charset "utf-8"}})
        (meta' {:attrs {:name "viewport" :content "width=device-width, initial-scale=1"}})
        (meta' {:attrs {:id "ssr-stages" :content (pr-str ssr-stages)}})
        (style {:attrs {:innerHTML "body {margin: 0;}"}})
        (style {:attrs {:innerHTML "body * {box-sizing: border-box;}"}})
        (script {:attrs {:id "config" :type "text/edn" :innerHTML (pr-str data)}}))
      (body {}
        (div {:attrs {:id "app"}})
        ; (script {:attrs {:src "http://repo/quamolit/libs/three.js"}})
        (script {:attrs {:src "https://cdnjs.cloudflare.com/ajax/libs/three.js/84/three.min.js"}})
        (script {:attrs {:src "object3d.js"}})
        (script {:attrs {:src "main.js"}})))))

(defn generate-html [ssr-stages]
  (let [ tree (comp-container {} ssr-stages)
         html-content (make-string tree)]
    (html-dsl {:build? true} html-content ssr-stages)))

(defn generate-empty-html []
  (html-dsl {:build? true} "" {}))

(defn spit [file-name content]
  (let [fs (js/require "fs")]
    (.writeFileSync fs file-name content)))

(defn -main []
  (if (= js/process.env.env "dev")
    (spit "target/dev.html" (generate-empty-html))
    (spit "target/index.html" (generate-empty-html))))

(-main)
