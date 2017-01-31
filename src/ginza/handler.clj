(ns ginza.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]
            [ginza.state :as state]
            [ginza.oauth :as oauth]
            [hiccup.core :refer :all]
            [ginza.office365api :as office365]
            [clojure.data.json :as json]))

(defroutes app-routes
  (GET "/" []
    (html [:html
             [:head
              [:title "Ginza"]
              [:link {:rel "stylesheet" :type "text/css" :href "main.css"}]
              [:link {:href "https://fonts.googleapis.com/css?family=Inconsolata"
                      :rel "stylesheet"}]]

             [:link {:rel "stylesheet" :href "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"
                     :integrity "sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u"
                     :crossorigin "anonymous"}]

             [:link {:rel "stylesheet" :href "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css"
                     :integrity "sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp"
                     :crossorigin "anonymous"}]



             [:body
                [:div#root]

              [:script {:src "https://code.jquery.com/jquery-3.1.1.min.js"
                        :integrity "sha256-hVVnYaiADRTO2PzUGmuLJr8BLUSjGIZsDYGmIJLv2b8="
                        :crossorigin "anonymous"}]
              [:script {:src "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"
                        :integrity "sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa"
                        :crossorigin "anonymous"}]
              [:script {:type "text/javascript" :src "js/bundle.js"}]]]))



  (GET "/oauth" request
    (if (contains? (:params request) :code)
      (oauth/request-access-token (:code (:params request)))
      "No valid code returned"))

  (GET "/test.xml" []
    (office365/api-get-contacts))
  (GET "/test2.xml" []
           {:status 200
            :headers {"Content-Type" "text/xml;charset=UTF-8"}
            :body "<?xml version=\"1.0\" encoding=\"UTF-8\"?><YealinkIPPhoneBook><Title>Yealink</Title><Menu Name=\"Default\"><Unit Name=\"Weber Dennis\"  Phone1=\"015112727388\" /><Unit Name=\"Purmann Ronny\"  Phone1=\"070728919003\" /><Unit Name=\"Trenkle Simon\"  Phone1=\"074238090823\"  Phone2=\"015771320842\" /><Unit Name=\"Kirsch Wolfgang\"  Phone1=\"0791509–326\" /><Unit Name=\"Schur Marcus\"  Phone1=\"01722515261\" /><Unit Name=\"Braunstein Elisabeth\"  Phone1=\"074238090824\" /><Unit Name=\" \"  Phone1=\"07721 22031\" /><Unit Name=\"Schmidtberger Adrian\"  Phone1=\"01747393304\" /><Unit Name=\"Wende Grischa\"  Phone1=\"070728919006\" /><Unit Name=\"Bippus Sascha\"  Phone1=\"070728919004\"  Phone2=\"14\" /><Unit Name=\"Purmann Ronny\"  Phone1=\"070728919003\"  Phone2=\"13\" /><Unit Name=\"Lang Dietmar\"  Phone1=\"11\"  Phone2=\"01705401576\" /><Unit Name=\"Schnitzler Stefan\"  Phone1=\"0711806089949\"  Phone2=\"071180608919\" /><Unit Name=\"Lang Sabine\"  Phone1=\"070728919002\"  Phone2=\"12\" /><Unit Name=\"Samsel Thomas\"  Phone1=\"061515002412\" /><Unit Name=\"Vertrieb J&amp;T\"  Phone1=\"07071968785\" /><Unit Name=\"Maucher Christian\"  Phone1=\"015117167667\" /><Unit Name=\"Koch Michael\"  Phone1=\"070728919000\" /><Unit Name=\"Deiber Benjamin\"  Phone1=\"15\"  Phone2=\"070728919005\"  Phone3=\"070728919005\"  Phone4=\"016097548040\" /><Unit Name=\"Cantepe Koray\"  Phone1=\"071819643100\" /><Unit Name=\"Schnitzler \"  Phone1=\"0711806089949\" /><Unit Name=\"Cantepe Koray\"  Phone1=\"071819643100\" /><Unit Name=\"Scherer Andreas\"  Phone1=\"074238090821\"  Phone2=\"001799109793\" /><Unit Name=\"Ulrich Dirk\"  Phone1=\"0934223815\"  Phone2=\"016097965633\" /><Unit Name=\"Pfau Philipp\"  Phone1=\"074238090822\"  Phone2=\"015231079657\" /><Unit Name=\"Lang Sabine\"  Phone1=\"070728919002\" /><Unit Name=\"Lang Dietmar\"  Phone1=\"070728919001\" /><Unit Name=\"Pfau Philipp\"  Phone1=\"074238090822\"  Phone2=\"015231079657\" /><Unit Name=\"Leonhardt Steffen\"  Phone1=\"01637118091\" /><Unit Name=\"Scherer Andreas\"  Phone1=\"074238090821\"  Phone2=\"001799109793\" /><Unit Name=\"Grimm Peggy\"  Phone1=\"0934285600\" /><Unit Name=\"Schnitzler Stefan\"  Phone1=\"0711806089949\"  Phone2=\"071180608919\"  Phone3=\"01637118069\" /><Unit Name=\"Trenkle Simon\"  Phone1=\"074238090823\"  Phone2=\"01711760414\" /><Unit Name=\"Mueller Toni\"  Phone1=\"061515002285\" /></Menu></YealinkIPPhoneBook>"})
  (route/not-found "Not Found"))


(defn wrap-api-defaults [status body]
  "Wraps maps to send json response"
  {:status status
   :headers {"Content-Type" "application/json charset=utf-8"}
   :body (json/write-str body)})


(defroutes api
  (context "/api" []
    (GET "/status" [] (wrap-api-defaults 200 {:connected (state/is-connected?)}))
    (GET "/getAuthLink" [] (wrap-api-defaults 200 {:link oauth/req-token-link}))
    (GET "/contacts" [] (wrap-api-defaults 200 {:data (office365/api-get-contacts-json)}))))


(def handler-static
  (wrap-defaults app-routes site-defaults))

(def handler-api
  (wrap-defaults api api-defaults))

(def app
  (routes
    handler-api
    handler-static))

