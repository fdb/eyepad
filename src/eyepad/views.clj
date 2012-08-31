(ns eyepad.views
  (:use [noir.core :only [defpartial defpage]]
        [noir.response :only [redirect]]
        [hiccup.page :only [include-css include-js html5]]
        [somnium.congomongo]
        [eyepad.models]))

;;;; DB connection ;;;;

(def mongo-url
  (or
    (System/getenv "MONGOHQ_URL")
    "mongodb://127.0.0.1:27017/eyepad"))

(def mongo-conn (make-connection mongo-url))
(create-collection! :snaphots)
(create-collection! :blobs)
(add-index! :blobs [:sha] :unique true)
(set-connection! mongo-conn)

;;;; ID Generation ;;;;

(def valid-id-chars "abcdefghjkmnpqrstuvwxyz123456789")

(defn generate-unique-id []
  "Generate a unique, random page ID."
  (let [random-seq (repeatedly #(rand-nth valid-id-chars))]
    (apply str (take 7 random-seq))))

;;;; Pages ;;;;

(defpartial layout [& content]
  (html5
    [:head
      [:title "eyepad"]
      (include-css "/css/normalize.css")
      (include-css "/css/eyepad.css")
      (include-css "/codemirror/codemirror.css")
      (include-js "/codemirror/codemirror.js")
      (include-js "/codemirror/clojure.js")
      (include-js "/js/jquery-1.8.0.min.js")
      (include-js "/js/eyepad.js")]
    [:body
      [:div#wrapper
        content]]))

(defpage "/" []
  (layout
    [:h1 "Eyepad" [:small "A Clojure playground."]]
    [:a.button {:href "/new" } "Create new"]))

(def initial-code "(+ 40 2)")

(defpage "/:id" {:keys [id]}
  (let [code (load-latest-code id)]

  (layout
    [:h1 (str "Page " id)]
    [:textarea#code (or code initial-code)]
    [:div#result]
    [:hr.clear]
    [:script (format "eyepad.init(\"%s\");" id)])))

(defpage "/new" []
  (redirect (str "/" (generate-unique-id))))

(defpage [:post "/eval/:id"] {:keys [id code]}
  (try
    (let [result (str (load-string code))]
      (when (not (= code initial-code))
        (save-snapshot! id code))
      result)
    (catch Exception e (str "Error: " (.getMessage e)))))


