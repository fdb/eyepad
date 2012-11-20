(ns eyepad.awt
  (:import [java.awt Color])
  (:import [java.awt.geom GeneralPath Point2D$Double Rectangle2D$Double]))

(defn to-rectangle2d
  "Convert a list of x/y/w/h to a Rectangle2D."
  ([x y w h] (Rectangle2D$Double. x y w h))
  ([[x y w h]] (to-rectangle2d x y w h)))

(defn to-point2d
  "Convert x/y coordinates to a Point2D."
  ([x y] (Point2D$Double. x y))
  ([[x y]] (to-point2d x y)))

(defn rgb-to-color
  "Convert RGB values to a Color."
  ([r g b] (Color. r g b))
  ([r g b a] (Color. r g b a)))

(defn hsb-to-color
  "Convert HSB values to a Color."
  ([r g b] (Color. r g b))
  ([r g b a] (Color. r g b a)))

(defn commands-to-path [commands]
  "Convert a list of moveto/lineto/curveto commands to a GeneralPath"
  (let [g (new GeneralPath)]
    (doseq [c commands]
      (let [coords (map float (rest c))]
        (condp = (first c)
          :moveto (.moveTo g (nth coords 0) (nth coords 1))
          :lineto (.lineTo g (nth coords 0) (nth coords 1))
          :curveto (.curveTo g (nth coords 0) (nth coords 1)
                             (nth coords 2) (nth coords 3)
                             (nth coords 4) (nth coords 5))
          :close (.closePath g)
          (throw (Exception. (str "Unknown command " c))))))
    g))

