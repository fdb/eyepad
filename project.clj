(defproject eyepad "0.1.0-SNAPSHOT"
            :description "A visual scratchpad for Clojure."
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [compojure "1.1.3"]
                           [ring "1.1.6"]
                           [hiccup "1.0.2"]
                           [congomongo "0.1.10"]
                           [org.clojure/core.logic "0.8.0-beta2"]]
            :min-lein-version "2.0.0"
            :plugins [[lein-ring "0.7.5"]]
            :ring {:handler eyepad.views/app}
            :main eyepad.views)

