(ns ginza.core
  (:require [clojure.browser.repl :as repl]
            [enfocus.core :as ef]))


(defn ^:export init []
      (repl/connect "http://localhost:9000/repl")
      (js/alert (str true)))