(ns eyepad.visualize
  (:use [hiccup.core :only [html]])
  (:require [eyepad.svg :as svg]))

;; =============================================================================
;; Type Matchers

(defn- determine-real-type [v]
  (cond 
    (number? v) :number
    (string? v) :string
    (nil? v) :nil
    (and (vector? v) (= 2 (count v)) (every? number? v)) :point
    (and (vector? v) (= 3 (count v)) (every? number? v)) :color
    (and (vector? v) (vector? (first v)) (= (ffirst v) :moveto)) :path
    (and (vector? v) (= (first v) :text)) :text
    (and (vector? v) (= (first v) :line)) :line
    :else :value))

(defn determine-type [v]
  "Examine an object and return its type and cardinality."
  (let [t (determine-real-type v)]
    ; Check if this form matches directly (that is, not the default clause.)
    (if (not= t :value)
      {:cardinality :single :type t}
      ; If not, try to take its first value and match that.
      (try
        {:cardinality :multiple :type (determine-real-type (first v))}
        (catch IllegalArgumentException e {:cardinality :single :type :value})))))

;; =============================================================================
;; Visualization Functions
;; All functions take in a sequence and return a hiccup data structure.

(defn draw-filled-rect [x y w h fill]
  [:rect {:x x :y y :width w :height h :fill fill}])

(defn draw-dot [[x y]]
  (let [sz 4
        r (/ sz 2)
        x0 (- x r)
        y0 (- y r)]
    (draw-filled-rect x0 y0 r r "#009dec")))

(defn draw-points [points]
  [:svg (map draw-dot points)])

(defn draw-swatch [[r g b] x y]
  "Draw a color swatch."
  (let [fill (svg/format-color r g b)]
    (draw-filled-rect x y 20 20 fill)))

(defn draw-colors [colors]
  (let [width 25
        total-width (* (count colors) width)
        xs (range 0 total-width width)]
        [:svg (map draw-swatch colors xs (repeat 0))]))

(defn draw-paths [paths]
  [:svg (map svg/format-path paths)])

(defn draw-lines [lines]
  (apply vector :svg lines))

(defn draw-texts [texts]
  (apply vector :svg texts))

(defn- draw-string [v]
  [:pre (str v)])

(defn draw-strings [values]
  (map draw-string values))

;; =============================================================================
;; Top-level Methods

(def visualizers
  {:point draw-points
   :color draw-colors
   :line draw-lines
   :path draw-paths
   :text draw-texts
   :string draw-strings
   :number draw-strings
   :nil draw-strings
   :value draw-strings})


(defn visualize [v]
  (let [t (determine-type v)
        type (:type t)
        cardinality (:cardinality t)
        visualizer (type visualizers)]
    (html (if (= cardinality :single)
      (visualizer [v])
      (visualizer v)))))

(comment

  (println (determine-type [[10 20] [30 40] [50 60]]))
  (println (determine-type [[10 20 30] [30 40 50]]))
  (println (determine-type [[:moveto 0 0] [:lineto 100 100]]))

)

;  (println (determine-type [[:line {:x1 10 :y1 10 :x2 234 :y2 2134}]]))