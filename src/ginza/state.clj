(ns ginza.state)

(def state (atom :not-connected))
(def token (atom '{}))

(defn is-connected? []
  (if (= @state :connected)
    (let [currentTime (->> (System/currentTimeMillis)
                        (/ 1000))
          tokenTime (Integer/parseInt (:expires_on @token))]
      (println currentTime)
      (println tokenTime)
      (if (> currentTime tokenTime)
        (do
          (println "Token expired")
          false)
        (do
          (println "Token still valid")
          true)))
    false))

(defn set-status-connected []
  (reset! state :connected))

(defn set-status-disconnected []
  (reset! state :not-connected))

(defn set-token [new-token]
  (reset! token new-token)
  (println @token))

(defn get-token []
  @token)