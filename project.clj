(defproject openio-tools "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "RELEX Proprietary License"
            :url ""}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [io.openio.sds/openio-api "0.6.2"]]
  :profiles {:drop {:main openio-tools.drop}
             :populate {:main openio-tools.populate}}
  :aliases {"drop" ["with-profile" "drop" "run"]
            "populate" ["with-profile" "populate" "run"]}
  :target-path "target/%s")
