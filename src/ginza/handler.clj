(ns ginza.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ginza.state :as state]))

(defroutes app-routes
  (GET "/" [] (str (state/is-connected?)))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
