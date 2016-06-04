
(set-env!
 :dependencies '[[org.clojure/clojurescript "1.9.14"      :scope "test"]
                 [org.clojure/clojure       "1.8.0"       :scope "test"]
                 [adzerk/boot-cljs          "1.7.170-3"   :scope "test"]
                 [adzerk/boot-reload        "0.4.6"       :scope "test"]
                 [cirru/boot-cirru-sepal    "0.1.7"       :scope "test"]
                 [binaryage/devtools        "0.5.2"       :scope "test"]
                 [mvc-works/hsl             "0.1.2"]
                 [mvc-works/respo           "0.1.22"]
                 [cljsjs/three              "0.0.76-0"]])

(require '[adzerk.boot-cljs   :refer [cljs]]
         '[adzerk.boot-reload :refer [reload]]
         '[respo.alias        :refer [html head title script style meta' div link body canvas]]
         '[respo.render.static-html :refer [make-html]]
         '[cirru-sepal.core   :refer [transform-cirru]]
         '[clojure.java.io    :as    io])

(def +version+ "0.1.0")

(task-options!
  pom {:project     'quamolit/quatrefoil
       :version     +version+
       :description "quatrefoil"
       :url         "https://github.com/quamolit/quatrefoil"
       :scm         {:url "https://github.com/quamolit/quatrefoil"}
       :license     {"MIT" "http://opensource.org/licenses/mit-license.php"}})

(deftask compile-cirru []
  (set-env!
    :source-paths #{"cirru/"})
  (comp
    (transform-cirru)
    (target :dir #{"compiled/"})))

(defn use-text [x] {:attrs {:innerHTML x}})
(defn html-dsl [data fileset]
  (make-html
    (html {}
    (head {}
      (title (use-text "Quamolit"))
      (link {:attrs {:rel "icon" :type "image/png" :href ""}})
      (if (:build? data)
        (link (:attrs {:rel "manifest" :href "manifest.json"})))
      (meta'{:attrs {:charset "utf-8"}})
      (meta' {:attrs {:name "viewport" :content "width=device-width, initial-scale=1"}})
      (style (use-text "body {margin: 0;}"))
      (style (use-text "body * {box-sizing: border-box;}"))
      (script {:attrs {:id "config" :type "text/edn" :innerHTML (pr-str data)}}))
    (body {}
      (canvas {:attrs {:id "app"}})
      (script {:attrs {:src "main.js"}})))))

(deftask html-file
  "task to generate HTML file"
  [d data VAL edn "data piece for rendering"]
  (with-pre-wrap fileset
    (let [tmp (tmp-dir!)
          out (io/file tmp "index.html")]
      (empty-dir! tmp)
      (spit out (html-dsl data fileset))
      (-> fileset
        (add-resource tmp)
        (commit!)))))

(deftask dev []
  (set-env!
    :asset-paths #{"assets"}
    :source-paths #{"cirru-src"})
  (comp
    (html-file :data {:build? false})
    (watch)
    (transform-cirru)
    (reload :on-jsload 'quatrefoil.core/on-jsload)
    (cljs)
    (target)))

(deftask build-simple []
  (set-env!
    :asset-paths #{"assets"}
    :source-paths #{"cirru-src"})
  (comp
    (transform-cirru)
    (cljs :optimizations :simple)
    (html-file :data {:build? false})
    (target)))

(deftask build-advanced []
  (set-env!
    :asset-paths #{"assets"}
    :source-paths #{"cirru-src"})
  (comp
    (transform-cirru)
    (cljs :optimizations :advanced)
    (html-file :data {:build? true})
    (target)))

(deftask rsync []
  (fn [next-task]
    (fn [fileset]
      (sh "rsync" "-r" "target/" "tiye:repo/quamolit/quatrefoil" "--exclude" "main.out" "--delete")
      (next-task fileset))))

(deftask send-tiye []
  (comp
    (build-simple)
    (rsync)))

(deftask build []
  (set-env!
    :source-paths #{"cirru-src"})
  (comp
    (compile-cirru)
    (pom)
    (jar)
    (install)
    (target)))

(deftask deploy []
  (set-env!
    :repositories #(conj % ["clojars" {:url "https://clojars.org/repo/"}]))
  (comp
    (build)
    (push :repo "clojars" :gpg-sign (not (.endsWith +version+ "-SNAPSHOT")))))
