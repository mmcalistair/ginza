(ns ginza.office365api
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [ginza.state :as state]))

(defn api-get-contacts []
  (println (:access_token (state/get-token)))
  (let [res (client/get "https://graph.microsoft.com/v1.0/me/contacts"
              {:headers
                 {:Authorization (:access_token (state/get-token))
                  :Content-Type "application/json"}})]
    (let [data (:value (json/read-json (:body res) true))]
      (str data))))
