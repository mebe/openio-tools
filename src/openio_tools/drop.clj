(ns openio-tools.drop
  (:gen-class)
  (:require [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]])
  (:import (io.openio.sds ClientBuilder)
           (io.openio.sds.models OioUrl)
           (io.openio.sds.models ListOptions)))

(def cli-options [["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Drop all data in a container. The container itself won't be deleted."
        ""
        "Usage: drop [options] <endpoint> <namespace> <account> <container>"
        ""
        "Options:"
        options-summary
        ""
        "Required arguments:"
        "  <endpoint>            URL of the OpenIO proxyd you want to interact with"
        "  <namespace>           OpenIO namespace"
        "  <account>             OpenIO account"
        "  <container>           The container you want to get rid of"]
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
          container-url (OioUrl/url account container)
          client (ClientBuilder/newClient namespace endpoint)
          objects (.objects (.listContainer client container-url (ListOptions.)))]
      (doseq [object objects]
        (let [object-url (OioUrl/url account container (.name object))]
          (.deleteObject client object-url))))))
