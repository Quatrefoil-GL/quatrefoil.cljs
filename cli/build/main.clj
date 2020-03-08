
(ns build.main
  (:require [shadow.cljs.devtools.api :as shadow]
            [clojure.java.shell :refer [sh]]))

(defn sh! [command]
  (println command)
  (println (:out (sh "bash" "-c" command))))

(defn build-cdn []
  (sh! "rm -rf dist/*")
  (shadow/release :client)
  (sh! "cp entry/* dist/"))

(defn build []
  (sh! "rm -rf dist/*")
  (shadow/release :client)
  (sh! "cp entry/* dist/"))
