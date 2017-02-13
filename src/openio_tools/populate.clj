(ns openio-tools.populate
  (:gen-class)
  (:require [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]])
  (:import (io.openio.sds ClientBuilder)
           (io.openio.sds.models OioUrl)
           (io.openio.sds.models ListOptions)))

(def cli-options [["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Sync a directory structure into OpenIO. Useful for uploading a DB."
        ""
        "Usage: sync [options] <endpoint> <namespace> <account> <container> <path>"
        ""
        "Options:"
        options-summary
        ""
        "Required arguments:"
        "  <endpoint>            URL of the OpenIO proxyd you want to interact with"
        "  <namespace>           OpenIO namespace"
        "  <account>             OpenIO account"
        "  <container>           The container you want to populate"
        "  <path>                The path from which you want to populate the container"]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\\n\\n"
       (string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 (usage summary))
      (< (count arguments) 4) (exit 1 (usage summary))
      errors (exit 1 (error-msg errors)))
    (let [endpoint (first arguments)
          namespace (second arguments)
          account (nth arguments 2)
          container (nth arguments 3)
          rawPath (nth arguments 4)
          path (str (.getAbsolutePath (clojure.java.io/file rawPath)) "/")
          client (ClientBuilder/newClient namespace endpoint)
          files (filter #(.isFile %) (file-seq (clojure.java.io/file path)))]
      (doseq [file files]
        (let [key (clojure.string/replace-first (.getAbsolutePath file) path "")
              object-url (OioUrl/url account container key)
              length (.length file)]
             (print (str "Putting " file " to " key "..."))
             (flush)
             (.putObject client object-url length file)
             (println " done!"))))))
