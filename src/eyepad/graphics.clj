(ns eyepad.graphics)

;; =============================================================================
;; Utility Functions

(defn to-radians [degrees]
  "Convert the angle from degrees to radians."
  (/ (* degrees Math/PI) 180))

(defn to-degrees [radians]
  "Convert the angle from radians to degrees."
  (/ (* radians 180) Math/PI))

(defn angle [x0 y0 x1 y1]
  "Calculate the angle (in degrees) between two points."
  (to-degrees (Math/atan2 (- y1 y0) (- x1 x0))))

(defn distance [x0 y0 x1 y1]
  "Calculate the distance between two points."
  (Math/sqrt 
    (+
      (Math/pow (- x1 x0) 2)
      (Math/pow (- y1 y0) 2))))

(defn coordinates [x y angle distance]
  "Calculate coordinates based on a point and angle/distance."
  (let [ra (to-radians angle)]
    [(+ x (* (Math/cos ra) distance))
     (+ y (* (Math/sin ra) distance))]))

(defn reflect [x0 y0 x1 y1 my-angle my-distance]
  "Reflect a point through another point."
  (let [a (+ my-angle (angle x0 y0 x1 y1))
        d (* my-distance (distance x0 y0 x1 y1))]
    (coordinates x0 y0 a d)))

(defn line-length [x0 y0 x1 y1]
  "Calculate the length of the line."
  (let [x (Math/pow (Math/abs (- x0 x1)) 2)
        y (Math/pow (Math/abs (- y0 y1)) 2)]
    (Math/sqrt (+ x y))))

(defn line-point [t x0 y0 x1 y1]
  "Calculate location for position t on the line."
  [(+ x0 (* t (- x1 x0)))
   (+ y0 (* t (- y1 y0)))])

(defn curve-length 
  "Calculate the length of the curve segment by integrating it."
  ([x0 y0 x1 y1 x2 y2 x3 y3] (curve-length x0 y0 x1 y1 x2 y2 x3 y3 20))
  ([x0 y0 x1 y1 x2 y2 x3 y3 n]
    ))

(defn curve-point [t x0 y0 x1 y1 x2 y2 x3 y3]
  "Calculate location for position t on the curve segment."
  (let [mint (- 1 t)
        x01 (+ (* x0 mint) (* x1 t))
        y01 (+ (* y0 mint) (* y1 t))
        x12 (+ (* x1 mint) (* x2 t))
        y12 (+ (* y1 mint) (* y2 t))
        x23 (+ (* x2 mint) (* x3 t))
        y23 (+ (* y2 mint) (* y3 t))
        c1x (+ (* x01 mint) (* x12 t))
        c1y (+ (* y01 mint) (* y12 t))
        c2x (+ (* x12 mint) (* x23 t))
        c2y (+ (* y12 mint) (* y23 t))
        x (+ (* c1x mint) (* c2x t))
        y (+ (* c1y mint) (* c2y t))]
    [x y]))

(defn path-segments [p]
  {:pre [(= (ffirst p) :moveto )]}
  "Convert path commands to line / curve segments."
  (loop [segments []
         cmds p
         moveto nil
         prev nil]
    (if-let [cmd (first cmds)]
      (condp = (first cmd)
        :moveto
        (recur segments
               (rest cmds)
               (rest cmd)
               (rest cmd))
        :lineto
        (recur (conj segments (flatten [:line prev (rest cmd)]))
               (rest cmds)
               moveto
               (rest cmd))
        :curveto
        (recur (conj segments (flatten [:curve prev (rest cmd)]))
               (rest cmds)
               moveto
               (rest cmd))
        :close
        (recur (conj segments (flatten [:line prev moveto]))
               (rest cmds)
               nil
               nil))
      segments)))

(defn segment-lengths [p]
  "Calculate the lengths of each path segment."
  (let [segments (path-segments p)
        length-fns {:line line-length :curve curve-length}]
    (map (fn [seg] (apply (length-fns (first seg)) (rest seg))) segments)))

(defn path-length [p]
  "Calculate the total length of the path."
  (reduce + (segment-lengths p)))

(defn grid [cols rows x y w h]
  "Create a sequence of points arranged in a grid.
  cols - The number of columns.
  rows - The number of rows.
  x - The starting position of the first column.
  y - The starting position of the first row.
  w - The width of a column.
  h - The height of a column."
  (for [iy (range rows)
        ix (range cols)]
    [(+ (* ix w) x) (+ (* iy h) y)]))

;; =============================================================================
;; Generators - Functions that create new shapes

(defn line-angle [x0 y0 angle distance]
  "Draw a line between a point and angle/distance."
  (let [[x1 y1] (coordinates x0 y0 angle distance)]
    [[:moveto x0 y0] [:lineto x1 y1]]))
(defn line
  "Create a line between two points."
  ([[x1 y1] [x2 y2]] (line {:x1 x1 :y1 y1 :x2 x2 :y2 y2}))
  ; attrs is a list here, so this won't work.
  ([x1 y1 x2 y2 & [attrs]] (line (assoc (or attrs {}) :x1 x1 :y1 y1 :x2 x2 :y2 y2)))
  ([attrs] [:line attrs]))

(defn line-angle [x1 y1 angle distance]
  "Create a line between a point and angle/distance."
  (let [[x2 y2] (coordinates x1 y1 angle distance)]
    (line x1 y1 x2 y2)))

(defn rect 
  "Create a path describing a rectangle."
  ([[x0 y0] width height] (rect x0 y0 width height))
  ([x0 y0 width height]
    (let [x1 (+ x0 width)
          y1 (+ y0 height)]
       [[:moveto x0 y0]
        [:lineto x1 y0]
        [:lineto x1 y1]
        [:lineto x0 y1]
        [:close]])))

(defn text
  "Create a piece of text."
  ([s x y] (text s {:x x :y y}))
  ([s x y attrs] (text s (assoc attrs :x x :y y)))
  ([s attrs] [:text attrs s]));; =============================================================================
;; Filters - Functions that modify/transform existing shapes
