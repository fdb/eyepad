(ns eyepad.tests
  (:use clojure.test)
  (:use eyepad.visualize))

;; =============================================================================
;; Type Matching

(defmacro deftermine [name data result]
  `(deftest ~name
     (let [d# ~data]
      (is (= (determine-type d#) ~result)))))

(deftermine determine-single-point 
  [10 20]
  {:type :point :cardinality :single})

(deftermine determine-multiple-points
  [[10 20] [30 40]]
  {:type :point :cardinality :multiple})

(deftermine determine-single-color
  [255 255 0]
  {:type :color :cardinality :single})

(deftermine determine-multiple-colors
  [[255 0 0] [0 255 0]]
  {:type :color :cardinality :multiple})

(deftermine determine-single-path
  [[:moveto 10 10] [:lineto 100 100]]
  {:type :path :cardinality :single})

(deftermine determine-multiple-paths
  [[[:moveto 10 10] [:lineto 100 100]]
   [[:moveto 100 100] [:lineto 200 200]]]
  {:type :path :cardinality :multiple})

(deftermine determine-single-string
  "test"
  {:type :string :cardinality :single})

(deftermine determine-multiple-strings
  ["a" "b"]
  {:type :string :cardinality :multiple})

(deftermine determine-single-number
  12
  {:type :number :cardinality :single})

(deftermine determine-multiple-numbers
  ; Gotcha: this matches as a single point.
  [1 2]
  {:type :point :cardinality :single})

(deftermine determine-single-nil
  nil
  {:type :nil :cardinality :single})

(deftermine determine-multiple-nils
  [nil nil]
  {:type :nil :cardinality :multiple})
