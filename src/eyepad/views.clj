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
            [:a.button {:href "/new"} "Add New Pad"]]]]
      [:div#content
        [:div.container
          content]]]))

(defpage "/" []
  (layout "EyePad"
    [:header#page-header
      [:h1 "A playground for visual code."]
      [:p "Program in Clojure and EyePad will interpret and visualize the output."]]
    [:a.button {:href "/new" } "Add New Pad"]))

(def initial-code "(+ 40 2)")

(def prefix-code "(ns user
  (:use [eyepad.graphics]
        [eyepad.svg]))
")

(defpage "/:id" {:keys [id]}
  (let [code (load-latest-code id)]

  (layout [:span "EyePad" [:small id]]
    [:textarea#code (or code initial-code)]
    [:div#result]
    [:hr.clear]
    [:script (format "eyepad.init(\"%s\");" id)])))

(defpage "/new" []
  (redirect (str "/" (generate-unique-id))))

(defpage [:post "/eval/:id"] {:keys [id code]}
  (try
    (let [clean-code (.trim code)
          prefixed-code (str prefix-code clean-code)
          result (str (load-string prefixed-code))]
      (when (not (= clean-code initial-code))
        (save-snapshot! id clean-code))
      result)
    (catch Exception e (str "Error: " (.getMessage e)))))


