(ns eyepad.graphics)

(defn to-radians [degrees]
  "Convert the angle from degrees to radians."
  (/ (* degrees Math/PI) 180))

(defn to-degrees [radians]
  "Convert the angle from radians to degrees."
  (/ (* radians 180) Math/PI))

(defn angle [[x0 y0] [x1 y1]]
  "Calculate the angle (in degrees) between two points."
  (to-degrees (Math/atan2 (- y1 y0) (- x1 x0))))

(defn distance [[x0 y0] [x1 y1]]
  "Calculate the distance between two points."
  (Math/sqrt 
    (+
      (Math/pow (- x1 x0) 2)
      (Math/pow (- y1 y0) 2))))

(defn coordinates [[x y] angle distance]
  "Calculate coordinates based on a point and angle/distance."
  (let [ra (to-radians angle)]
    [(+ x (* (Math/cos ra) distance))
     (+ y (* (Math/sin ra) distance))]))

(defn reflect [[x0 y0] [x1 y1] my-angle my-distance]
  "Reflect a point through another point."
  (let [a (+ my-angle (angle x0 y0 x1 y1))
        d (* my-distance (distance x0 y0 x1 y1))]
    (coordinates x0 y0 a d)))


(defn line-angle [[x0 y0 :as pos] angle distance]
  "Draw a line between a point and angle/distance."
  (let [[x1 y1] (coordinates pos angle distance)]
    {:d [
            [:moveto x0 y0]
            [:lineto x1 y1]]}))

(defn rect 
  "Create a path describing a rectangle."
  ([[x0 y0] width height] (rect x0 y0 width height))
  ([x0 y0 width height]
    (let [x1 (+ x0 width)
          y1 (+ y0 height)]
      {:d
       [[:moveto x0 y0]
        [:lineto x1 y0]
        [:lineto x1 y1]
        [:lineto x0 y1]
        [:close]]})))

