(ns eyepad.views
  (:use [noir.core :only [defpartial defpage]]
        [noir.response :only [redirect]]
        [hiccup.page :only [include-css include-js html5]]
        [somnium.congomongo]
        [eyepad.models]
        [eyepad.visualize :only [visualize]]))

;;;; DB connection ;;;;

(def mongo-url
  (or
    (System/getenv "MONGOHQ_URL")
    "mongodb://127.0.0.1:27017/eyepad"))

(def mongo-conn (make-connection mongo-url))
(set-connection! mongo-conn)

(create-collection! :snaphots)
(create-collection! :blobs)
(add-index! :blobs [:sha] :unique true)

;;;; ID Generation ;;;;

(def valid-id-chars "abcdefghjkmnpqrstuvwxyz123456789")

(defn generate-unique-id []
  "Generate a unique, random page ID."
  (let [random-seq (repeatedly #(rand-nth valid-id-chars))]
    (apply str (take 5 random-seq))))

;;;; Pages ;;;;

(defpartial layout [title & content]
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

(defpage "/" {}
  (layout "EyePad"
    [:header#page-header
      [:h1 "A playground for visual code."]
      [:p "Program in Clojure and EyePad will interpret and visualize the output."]]
    [:a.button {:href "/new" } "Create a New Pad"]))

(def initial-code "(+ 40 2)")

(def code-imports "(ns user
  (:use [eyepad.graphics]
        [eyepad.svg]))
")

(defn prefix-code [code]
  (str code-imports code))

(defpage "/:id" {:id id}
  (let [code (load-latest-code id)]

  (layout [:span "EyePad" [:small id]]
    [:textarea#code (or code initial-code)]
    [:div#result]
    [:hr.clear]
    [:script (format "eyepad.init(\"%s\");" id)])))

(defpage "/new" {}
  (redirect (str "/" (generate-unique-id))))

;(defn build-result [status value output]
;  "Build the evaluation result."
;  (jsonify {:status status :value value :output output}))

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

(defpage [:post "/eval/:id"] {:id id :code code}
  (let [clean-code (.trim code)
        prefixed-code (prefix-code code)
        result (visualize-code prefixed-code)]
    (when (not (= clean-code initial-code))
      (save-snapshot! id clean-code))
    result))
