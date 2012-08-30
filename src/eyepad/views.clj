(ns eyepad.views
  (:use [noir.core :only [defpartial defpage]]
        [noir.response :only [redirect]]
        [hiccup.page :only [include-css include-js html5]]))

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

(def valid-id-chars "abcdefghjkmnpqrstuvwxyz123456789")

(defn generate-unique-id []
  "Generate a unique, random page ID."
  (let [random-seq (repeatedly #(rand-nth valid-id-chars))]
    (apply str (take 7 random-seq))))

(defpage "/" []
  (layout
    [:h1 "Eyepad" [:small "A Clojure playground."]]
    [:a.button {:href "/new" } "Create new"]))


(def initial-code "(use '[hiccup.core :only [html]])

(html [:svg 
  [:rect {:x 10 :y 20 :width 30 :height 40 :fill \"green\"}]])")

(defpage "/:id" {:keys [id]}
  (layout
    [:h1 (str "Page " id)]
    [:textarea#code initial-code]
    [:div#result]
    [:hr.clear]
    [:script "eyepad.init();"]))

(defpage "/new" []
  (redirect (str "/" (generate-unique-id))))

(defpage [:post "/eval"] {:keys [code]}
  (try
    (str (load-string code))
    (catch Exception e (str "Error: " (.getMessage e)))))


