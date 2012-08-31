(ns eyepad.models
  (:use [somnium.congomongo])
  (:import [java.security MessageDigest]))

(defn get-digest-bytes [s]
 (.digest (MessageDigest/getInstance "SHA") (.getBytes s "UTF-8")))

(defn- bytes-to-hex [bytes]
  (apply str (map #(format "%02x" %) bytes)))

(defn sha1-hex-digest [s]
  "Create a SHA-1 digest of the contents."
  (bytes-to-hex (get-digest-bytes s)))

(defn save-snapshot! [id code]
  "Save a snapshot of the pad with the given ID."
  (let [code-sha (sha1-hex-digest code)]
    (insert! :snapshots {:id id :sha code-sha})
    (insert! :blobs {:sha code-sha :contents code})))

(defn load-latest-snapshot [id]
  "Load the latest pad snapshot."
  ; Fetch-one doesn't work correctly with sorting.
  (first (fetch :snapshots :where {:id id} :sort {:_id -1})))

(defn load-latest-code [id]
  "Load the latest pad code."
  (if-let [snapshot (load-latest-snapshot id)]
    (:contents (fetch-one :blobs :where {:sha (:sha snapshot)}))))

;(fetch-one )
; (fetch-one :blobs :where {:sha "abcd"})