Same form can match multiple elements,
e.g. [10 20 30] can match [R G B] but also [X Y Z].

Create simple operations on the path.

Styling as metadata on path:

(def standard-path [[:moveto 0 -100] [:lineto 100 100] [:lineto -100 100]])
(def colored-path (with-meta standard-path {:fill "red"}))
(meta colored-path)
;=> {:fill "red"}




TODO
====
- [editor] Highlight matching opening brace.
- [editor] Auto-insert closing brace.
- [eval] Capture stdout and display it.
- Run test.generative on core functions.