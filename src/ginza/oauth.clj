(ns ginza.oauth
  (:require [ring.util.response]
            [ginza.state :as state]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [victoria.core :as victoria]))

(def oauth-config (victoria/load-configuration "config.json"))

(def req-token-link
  (str "https://login.microsoftonline.com/common/oauth2/authorize?"
       "response_type=code"
       "&"
       "redirect_url=" (java.net.URLEncoder/encode (:redirect_url oauth-config) "utf-8")
       "&"
       "client_id=" (:app_id oauth-config)))

(defn refresh-token [body]
  (println "Start thread")
  (Thread/sleep 10000)
  (println "WAKE UP")
  (client/post))

(defn request-access-token [authorize-code]
  (println "TEST")
  (let [res (client/post "https://login.microsoftonline.com/common/oauth2/token"
               {:form-params
                  {:grant_type "authorization_code"
                   :redirect_url (:redirect_url oauth-config)
                   :client_id (:app_id oauth-config)
                   :client_secret (:app_secret oauth-config)
                   :code authorize-code
                   :resource "https://graph.microsoft.com/"}})]
       (println res)
       (if (contains? res :body)
         (let [body (json/read-json (:body res) true)]
           (println body)
           (state/set-token body)
           (future (refresh-token body))
           (state/set-status-connected))))
  (ring.util.response/redirect "/"))


