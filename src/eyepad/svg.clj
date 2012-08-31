(ns eyepad.svg
	(:use [hiccup.core :only [html]])
	(:import [java.util Locale]))

(defn format-point [[x y]]
  ; The locale is important for using the correct floating-point separator.
  ; SVG only accepts ".".
  (String/format Locale/US "%.1f %.1f" (to-array [(float x) (float y)])))

(defmulti format-command first)

(defmethod format-command :moveto [[command x y]]
  (str "M " (format-point [x y])))

(defmethod format-command :lineto [[command x y]]
  (str "L " (format-point [x y])))

(defmethod format-command :curveto [[command x0 y0 x1 y1 x2 y2]]
  (str "C " 
    (format-point [x0 y0])
    (format-point [x1 y1])
    (format-point [x2 y2])))

(defmethod format-command :close [_]
  "z")

(defn format-commands [commands]
  (apply str (map format-command commands)))


(defn format-path [path]
  [:path {:d (format-commands (:d path)) :fill "white" :stroke "black"}])

(defn draw-paths [paths]
  (html [:svg (map format-path paths)]))
