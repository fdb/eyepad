(ns eyepad.graphics)

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


(defn line-angle [x0 y0 angle distance]
  "Draw a line between a point and angle/distance."
  (let [[x1 y1] (coordinates x0 y0 angle distance)]
    [[:moveto x0 y0] [:lineto x1 y1]]))


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
  ([s attrs] [:text attrs s]))