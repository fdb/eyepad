(defproject eyepad "0.1.0-SNAPSHOT"
            :description "A visual scratchpad for Clojure."
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [compojure "1.1.3"]
                           [hiccup "1.0.2"]
                           [congomongo "0.1.10"]]
            :plugins [[lein-ring "0.7.5"]]
            :ring {:handler eyepad.views/app})

