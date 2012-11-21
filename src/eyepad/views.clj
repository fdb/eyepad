(ns eyepad.views
  (:use [compojure.core :only [defroutes GET POST]]
        [ring.adapter.jetty :only [run-jetty]]
        [ring.util.response :only [redirect]]
        [ring.middleware.params :only [wrap-params]]
        [hiccup.page :only [include-css include-js html5]]
        [somnium.congomongo]
        [eyepad.models]
        [eyepad.visualize :only [visualize]])
  (:require [compojure.route :as route]))

;; =============================================================================
;; ID Generation

(def valid-id-chars "abcdefghjkmnpqrstuvwxyz123456789")

(defn generate-unique-id []
  "Generate a unique, random page ID."
  (let [random-seq (repeatedly #(rand-nth valid-id-chars))]
    (apply str (take 5 random-seq))))


;; =============================================================================
;; Code Evaluation

(def initial-code "(+ 40 2)")

(def code-imports "(ns user
  (:use [eyepad.graphics]
        [eyepad.svg]))
")

(defn prefix-code [code]
  (str code-imports code))

(defn format-error [t]
  (str (.. t getClass getName) ": " (.getMessage t)))

(defn evaluate-code [code]
  "Evaluate the given code using Clojure load-string."
  (try
    (load-string code)
    (catch Throwable t (format-error t))))

(defn visualize-code [code]
  "Evaluate the code, then visualize it."
  (let [result (evaluate-code code)]
        (visualize result)))

;; =============================================================================
;; Views

(defn layout [title & content]
  (html5
    [:head
      [:title "EyePad"]
      (include-css "/css/normalize.css")
      (include-css "/css/eyepad.css")
      (include-css "/codemirror/codemirror.css")
      (include-js "/codemirror/codemirror.js")
      (include-js "/codemirror/clojure.js")
      (include-js "/js/jquery-1.8.0.min.js")
      (include-js "/js/eyepad.js")]
    [:body
      [:header#site-header
        [:div.container
          [:a {:href "/"} [:h1 title]]
          [:nav
            [:a.button {:href "/new"} "New Pad"]]]]
      [:div#content
        [:div.container
          content]]]))

(defn welcome []
  (layout "EyePad"
    [:header#page-header
      [:h1 "A playground for visual code."]
      [:p "Program in Clojure and EyePad will interpret and visualize the output."]]
    [:a.button {:href "/new" } "Create a New Pad"]))

(defn pad-create []
  (redirect (str "/" (generate-unique-id))))

(defn pad-detail [id]
  (let [code (load-latest-code id)]
    (layout [:span "EyePad" [:small id]]
      [:textarea#code (or code initial-code)]
      [:div#result]
      [:hr.clear]
      [:script (format "eyepad.init(\"%s\");" id)])))

(defn pad-eval [id code]
  (let [clean-code (.trim code)
        prefixed-code (prefix-code code)
        result (visualize-code prefixed-code)]
    (when (not (= clean-code initial-code))
      (save-snapshot! id clean-code))
    result))

(defroutes routes
  (GET "/" [] (welcome))
  (GET "/new" [] (pad-create))
  (GET "/:id" [id] (pad-detail id))
  (POST "/eval/:id" [id code] (pad-eval id code))
  (route/resources "/")
  (route/not-found "<h1>Page not found.</h1>"))

(def app (wrap-params routes))

(defn -main []
  (let [port (Integer/parseInt (get (System/getenv) "PORT" "8080"))]
    (run-jetty app {:port port})))