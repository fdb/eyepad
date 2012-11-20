(ns eyepad.svg
  (:import [java.util Locale]))

(defn- color-to-int [r g b]
  "Convert RGB components (0-255) to an integer by bit-shifting."
  (bit-or 
    (bit-shift-left (bit-and r 0xFF) 16)
    (bit-shift-left (bit-and g 0xFF) 8)
    (bit-shift-left (bit-and b 0xFF) 0)))

(defn format-color [r g b]
  "Convert a color in RGB (0-255) format to a hexadecimal value, like '#123456'."
  (format "#%06X" (color-to-int r g b)))

(defn format-point [x y]
  ; The locale is important for using the correct floating-point separator.
  ; SVG only accepts ".".
  (String/format Locale/US "%.1f %.1f" (to-array [(float x) (float y)])))

(defmulti format-command first)

(defmethod format-command :moveto [[_ x y]]
  (str "M " (format-point x y)))

(defmethod format-command :lineto [[_ x y]]
  (str "L " (format-point x y)))

(defmethod format-command :curveto [[_ x0 y0 x1 y1 x2 y2]]
  (str "C " 
    (format-point x0 y0)
    (format-point x1 y1)
    (format-point x2 y2)))

(defmethod format-command :close [_]
  "z")

(defn format-commands [commands]
  (apply str (map format-command commands)))

(defn format-path [path]
  (let [style (meta path)]
    [:path {
      :d (format-commands path) 
      :fill (or (:fill style) "white") 
      :stroke (or (:stroke style) "black")}]))
